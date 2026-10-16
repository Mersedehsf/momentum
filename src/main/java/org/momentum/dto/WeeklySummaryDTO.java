package org.momentum.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class WeeklySummaryDTO {

    private long totalCreated;
    private long completedCount;
    private long incompleteCount;
    private double completionRate;
    private LocalDate busiestCompletionDay;
    private Map<String, Long> minutesPerCategory = new LinkedHashMap<>();

    public long getTotalCreated() {
        return totalCreated;
    }

    public void setTotalCreated(long totalCreated) {
        this.totalCreated = totalCreated;
    }

    public long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(long completedCount) {
        this.completedCount = completedCount;
    }

    public long getIncompleteCount() {
        return incompleteCount;
    }

    public void setIncompleteCount(long incompleteCount) {
        this.incompleteCount = incompleteCount;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public LocalDate getBusiestCompletionDay() {
        return busiestCompletionDay;
    }

    public void setBusiestCompletionDay(LocalDate busiestCompletionDay) {
        this.busiestCompletionDay = busiestCompletionDay;
    }

    public Map<String, Long> getMinutesPerCategory() {
        return minutesPerCategory;
    }

    public void setMinutesPerCategory(Map<String, Long> minutesPerCategory) {
        this.minutesPerCategory = minutesPerCategory != null ? new LinkedHashMap<>(minutesPerCategory) : new LinkedHashMap<>();
    }
}
