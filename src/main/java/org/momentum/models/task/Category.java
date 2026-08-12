//package org.momentum.models.task;
//
//import jakarta.persistence.*;
//import org.momentum.models.BaseEntity;
//
//@Entity
//@Table(name = "category")
//public class Category extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "category_id")
//    private Long id;
//
//    @Column(name = "title")
//    private String title;
//
//    @Column(name = "estimatedMinutes")
//    private Integer estimatedMinutes;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public Integer getEstimatedMinutes() {
//        return estimatedMinutes;
//    }
//
//    public void setEstimatedMinutes(Integer estimatedMinutes) {
//        this.estimatedMinutes = estimatedMinutes;
//    }
//}
