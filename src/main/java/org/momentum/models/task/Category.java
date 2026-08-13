package org.momentum.models.task;

import jakarta.persistence.*;
import org.momentum.models.BaseEntity;

@Entity
@Table(name = "category")
public class Category extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "estimatedMinutes")
    private Integer estimatedMinutes;

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
}
