package org.momentum.models.task;


import jakarta.persistence.*;
import org.momentum.models.BaseEntity;

import java.time.Instant;

@Entity
@Table(name = "task")
public class Task extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "estimatedMinutes")
    private Integer estimatedMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "comment")
    private String comment;

    @Column(name = "completed")
    private Integer completed = 0;

    @Column(name = "completion_time")
    private Instant completionTime;

    public Task() {
    }

    public Task(String title, Integer estimatedMinutes, Category category, String comment, Integer completed) {
        this.title = title;
        this.estimatedMinutes = estimatedMinutes;
        this.category = category;
        this.comment = comment;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Instant getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Instant completionTime) {
        this.completionTime = completionTime;
    }

    @Override
    public String toString() {
        return """
            📝 Task

            title: %s
            estimatedMinutes: %s
            category: %s
            comment: %s
            """.formatted(
                title,
                estimatedMinutes,
                category,
                comment
        );
    }
}
