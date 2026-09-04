package org.momentum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.momentum.dto.WeeklySummaryDTO;
import org.momentum.models.task.Task;
import org.momentum.repos.TaskRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeklyAnalysisServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Tehran");

    @Mock
    private TaskRepository taskRepository;

    private WeeklyAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new WeeklyAnalysisService(taskRepository);
    }

    private Task task(Instant creationTime, Integer estimatedMinutes, Integer completed, Instant completionTime) {
        Task task = new Task();
        task.setCreationTime(creationTime);
        task.setEstimatedMinutes(estimatedMinutes);
        task.setCompleted(completed);
        task.setCompletionTime(completionTime);
        return task;
    }

    private Instant instant(int year, int month, int day, int hour, int minute) {
        return ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZONE).toInstant();
    }

    private void stubCreated(List<Task> tasks) {
        when(taskRepository.findCreatedBetween(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(tasks);
    }

    @Test
    void emptyWeek_producesZerosAndNullBusiestDays() {
        stubCreated(List.of());

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(0, summary.getTotalCreated());
        assertEquals(0, summary.getCompletedCount());
        assertEquals(0, summary.getIncompleteCount());
        assertEquals(0.0, summary.getCompletionRate());
        assertEquals(0, summary.getTotalEstimatedMinutes());
        assertEquals(0, summary.getCompletedEstimatedMinutes());
        assertNull(summary.getBusiestCreationDay());
        assertNull(summary.getBusiestCompletionDay());
    }

        @Test
        void allTasksIncomplete() {
            Task t1 = task(instant(2026, 1, 3, 10, 0), 30, 0, null);
            Task t2 = task(instant(2026, 1, 4, 12, 0), 45, 0, null);
            stubCreated(List.of(t1, t2));

            WeeklySummaryDTO summary = service.analyze();

            assertEquals(2, summary.getTotalCreated());
            assertEquals(0, summary.getCompletedCount());
            assertEquals(2, summary.getIncompleteCount());
            assertEquals(0.0, summary.getCompletionRate());
        }

    @Test
    void allTasksCompleted() {
        Task t1 = task(instant(2026, 1, 3, 10, 0), 30, 1, instant(2026, 1, 3, 18, 0));
        Task t2 = task(instant(2026, 1, 4, 12, 0), 45, 1, instant(2026, 1, 4, 20, 0));
        stubCreated(List.of(t1, t2));

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(2, summary.getTotalCreated());
        assertEquals(2, summary.getCompletedCount());
        assertEquals(0, summary.getIncompleteCount());
        assertEquals(100.0, summary.getCompletionRate(), 0.0001);
    }

    @Test
    void mixedCompletedAndIncomplete() {
        Task completed = task(instant(2026, 1, 3, 10, 0), 30, 1, instant(2026, 1, 3, 18, 0));
        Task incomplete1 = task(instant(2026, 1, 4, 12, 0), 45, 0, null);
        Task incomplete2 = task(instant(2026, 1, 5, 9, 0), 15, 0, null);
        stubCreated(List.of(completed, incomplete1, incomplete2));

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(3, summary.getTotalCreated());
        assertEquals(1, summary.getCompletedCount());
        assertEquals(2, summary.getIncompleteCount());
        assertEquals(100.0 / 3.0, summary.getCompletionRate(), 0.0001);
    }

    @Test
    void taskCreatedLastWeekButCompletedThisWeek_isExcluded() {
        Task thisWeek = task(instant(2026, 1, 3, 10, 0), 30, 1, instant(2026, 1, 3, 18, 0));
        stubCreated(List.of(thisWeek));

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(1, summary.getTotalCreated());
        assertEquals(1, summary.getCompletedCount());
        assertEquals(0, summary.getIncompleteCount());
        assertEquals(100.0, summary.getCompletionRate(), 0.0001);
        assertEquals(30, summary.getCompletedEstimatedMinutes());
    }

    @Test
    void busiestCreationDay_picksDayWithMostTasksCreated() {
        Task saturday1 = task(instant(2026, 1, 3, 10, 0), 10, 0, null);
        Task saturday2 = task(instant(2026, 1, 3, 11, 0), 10, 0, null);
        Task sunday = task(instant(2026, 1, 4, 12, 0), 10, 0, null);
        stubCreated(List.of(saturday1, saturday2, sunday));

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(LocalDate.of(2026, 1, 3), summary.getBusiestCreationDay());
    }

    @Test
    void busiestCompletionDay_picksDayWithMostCompletedTasks() {
        Task mondayCompleted1 = task(instant(2026, 1, 5, 9, 0), 10, 1, instant(2026, 1, 5, 15, 0));
        Task mondayCompleted2 = task(instant(2026, 1, 5, 10, 0), 10, 1, instant(2026, 1, 5, 16, 0));
        Task tuesdayCompleted = task(instant(2026, 1, 6, 9, 0), 10, 1, instant(2026, 1, 6, 15, 0));
        Task notCompleted = task(instant(2026, 1, 4, 9, 0), 10, 0, null);
        stubCreated(List.of(mondayCompleted1, mondayCompleted2, tuesdayCompleted, notCompleted));

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(LocalDate.of(2026, 1, 5), summary.getBusiestCompletionDay());
    }

    @Test
    void nullEstimatedMinutes_treatedAsZero() {
        Task t1 = task(instant(2026, 1, 3, 10, 0), null, 1, instant(2026, 1, 3, 18, 0));
        Task t2 = task(instant(2026, 1, 4, 12, 0), 50, 0, null);
        stubCreated(List.of(t1, t2));

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(50, summary.getTotalEstimatedMinutes());
        assertEquals(0, summary.getCompletedEstimatedMinutes());
    }

    @Test
    void totalEstimatedMinutes_sumsAllWeeklyTasks() {
        Task t1 = task(instant(2026, 1, 3, 10, 0), 30, 1, instant(2026, 1, 3, 18, 0));
        Task t2 = task(instant(2026, 1, 4, 12, 0), 45, 0, null);
        Task t3 = task(instant(2026, 1, 5, 9, 0), 25, 0, null);
        stubCreated(List.of(t1, t2, t3));

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(100, summary.getTotalEstimatedMinutes());
    }

    @Test
    void completedEstimatedMinutes_sumsOnlyCompletedWeeklyTasks() {
        Task completed1 = task(instant(2026, 1, 3, 10, 0), 30, 1, instant(2026, 1, 3, 18, 0));
        Task completed2 = task(instant(2026, 1, 4, 12, 0), 45, 1, instant(2026, 1, 4, 20, 0));
        Task incomplete = task(instant(2026, 1, 5, 9, 0), 60, 0, null);
        stubCreated(List.of(completed1, completed2, incomplete));

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(75, summary.getCompletedEstimatedMinutes());
    }

    @Test
    void completionRate_isCompletedDividedByTotalTimes100() {
        Task completed = task(instant(2026, 1, 3, 10, 0), 10, 1, instant(2026, 1, 3, 18, 0));
        Task incomplete1 = task(instant(2026, 1, 4, 12, 0), 10, 0, null);
        Task incomplete2 = task(instant(2026, 1, 5, 9, 0), 10, 0, null);
        stubCreated(List.of(completed, incomplete1, incomplete2));

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(100.0 / 3.0, summary.getCompletionRate(), 0.0001);
    }

    @Test
    void queryRange_startsAtSaturdayMidnightAndEndsAtNextSaturdayMidnight() {
        stubCreated(List.of());

        service.analyze();

        ArgumentCaptor<Instant> fromCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> toCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(taskRepository).findCreatedBetween(fromCaptor.capture(), toCaptor.capture());

        ZonedDateTime from = fromCaptor.getValue().atZone(ZONE);
        ZonedDateTime to = toCaptor.getValue().atZone(ZONE);

        assertEquals(6, from.getDayOfWeek().getValue());
        assertEquals(0, from.getHour());
        assertEquals(0, from.getMinute());
        assertEquals(0, from.getSecond());

        assertEquals(to.getDayOfWeek().getValue(), 6);
        assertEquals(to.toLocalDate(), from.toLocalDate().plusDays(7));
        assertEquals(0, to.getHour());
        assertEquals(0, to.getMinute());
        assertEquals(0, to.getSecond());
    }

    @Test
    void emptyWeek_completionRateIsZeroNoDivisionByZero() {
        stubCreated(List.of());

        WeeklySummaryDTO summary = service.analyze();

        assertEquals(0, summary.getTotalCreated());
        assertEquals(0.0, summary.getCompletionRate());
        assertEquals(0, summary.getCompletedEstimatedMinutes());
    }
}
