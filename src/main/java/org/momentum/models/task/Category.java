package org.momentum.models.task;

import jakarta.persistence.*;
import org.momentum.models.BaseEntity;

@Entity
@Table(name = "category")
public class Category extends BaseEntity {//todo handle deleting in edit as well

    @Column(name = "title")
    private String title;

    @Column(name = "estimatedMinutes")
    private Integer estimatedMinutes;

    public Category() {
    }

    public Category(String title, Integer deleted) {
       this.title = title;
       this.deleted = deleted;
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

    @Override
    public String toString() {
        return """
            📝 Category

            title: %s
            estimatedMinutes: %s
            deleted: %s
            """.formatted(
                title,
                estimatedMinutes,
               deleted
        );
    }
}
