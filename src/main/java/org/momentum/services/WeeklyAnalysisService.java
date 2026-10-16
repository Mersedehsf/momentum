package org.momentum.services;

import org.momentum.dto.WeeklySummaryDTO;
import org.momentum.models.task.Task;
import org.momentum.repos.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WeeklyAnalysisService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Tehran");

    private final TaskRepository taskRepository;

    public WeeklyAnalysisService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public WeeklySummaryDTO analyze() {

        ZonedDateTime now = ZonedDateTime.now(ZONE);

        LocalDate friday = currentFriday(now.toLocalDate());
        LocalDate saturday = friday.minusDays(6);
        LocalDate nextSaturday = friday.plusDays(1);

        Instant from = saturday.atStartOfDay(ZONE).toInstant();
        Instant to = nextSaturday.atStartOfDay(ZONE).toInstant();

        List<Task> created = taskRepository.findCreatedBetween(from, to);
        List<Task> completed = created.stream()
                .filter(task -> task.getCompleted() != null && task.getCompleted() == 1)
                .toList();

        WeeklySummaryDTO summary = new WeeklySummaryDTO();

        summary.setTotalCreated(created.size());
        summary.setCompletedCount(completed.size());

        long incomplete = created.size() - completed.size();
        summary.setIncompleteCount(Math.max(0, incomplete));

        summary.setCompletionRate(created.isEmpty() ? 0.0 : (completed.size() * 100.0) / created.size());

        summary.setBusiestCompletionDay(busiestCompletionDay(completed));

        summary.setMinutesPerCategory(minutesPerCategory(completed));

        return summary;
    }

    private long safeSum(List<Task> tasks) {
        return tasks.stream()
                .mapToLong(task -> task.getEstimatedMinutes() == null ? 0L : task.getEstimatedMinutes())
                .sum();
    }

    private LocalDate busiestCompletionDay(List<Task> tasks) {
        return busiestDay(tasks, false);
    }

    private Map<String, Long> minutesPerCategory(List<Task> completedTasks) {
        Map<String, Long> grouped = completedTasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getCategory() != null && task.getCategory().getTitle() != null
                                ? task.getCategory().getTitle()
                                : "Uncategorized",
                        Collectors.summingLong(task -> task.getEstimatedMinutes() == null ? 0L : task.getEstimatedMinutes())
                ));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private LocalDate busiestDay(List<Task> tasks, boolean useCreationTime) {

        Map<LocalDate, Long> counts = tasks.stream()
                .map(task -> useCreationTime ? task.getCreationTime() : task.getCompletionTime())
                .filter(instant -> instant != null)
                .map(instant -> instant.atZone(ZONE).toLocalDate())
                .collect(Collectors.groupingBy(date -> date, Collectors.counting()));

        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private LocalDate currentFriday(LocalDate today) {

        int dayOfWeek = today.getDayOfWeek().getValue();
        int daysUntilFriday = (5 - dayOfWeek + 7) % 7;
        return today.plusDays(daysUntilFriday);
    }

    public String format(WeeklySummaryDTO summary) {

        String completedDay = summary.getBusiestCompletionDay() == null ? "—" : summary.getBusiestCompletionDay().toString();
        String categorySection = formatCategoryMinutes(summary.getMinutesPerCategory());

        return """
                📊 Weekly Performance Summary

                📈 Completion rate: %.1f%%
                ✅ Completed: %d
                📝 Tasks created: %d
                ⏳ Incomplete: %d
                🎯 Busiest completion day: %s
                %s
                """.formatted(
                summary.getCompletionRate(),
                summary.getCompletedCount(),
                summary.getTotalCreated(),
                summary.getIncompleteCount(),
                completedDay,
                categorySection
        );
    }

    private String formatCategoryMinutes(Map<String, Long> minutesPerCategory) {
        if (minutesPerCategory == null || minutesPerCategory.isEmpty()) {
            return "⏱️ Time spent per category: —";
        }
        StringBuilder sb = new StringBuilder("⏱️ Time spent per category:");
        minutesPerCategory.forEach((category, minutes) ->
                sb.append("\n  • ").append(category).append(": ").append(minutes).append(" min")
        );
        long total = minutesPerCategory.values().stream().mapToLong(Long::longValue).sum();
        sb.append("\n  ─ Total: ").append(total).append(" min");
        return sb.toString();
    }
}
