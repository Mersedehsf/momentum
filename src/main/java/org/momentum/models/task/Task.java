//package org.momentum.models.task;
//
//
//import jakarta.persistence.*;
//import org.momentum.models.BaseEntity;
//
//@Entity
//@Table(name = "task")
//public class Task extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "task_id")
//    private Long id;
//
//    @Column(name = "title")
//    private String title;
//
//    @Column(name = "estimatedMinutes")
//    private Integer estimatedMinutes;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id")
//    private Category category;
//
//    @Column(name = "comment")
//    private String comment;
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
//
//    public Category getCategory() {
//        return category;
//    }
//
//    public void setCategory(Category category) {
//        this.category = category;
//    }
//
//    public String getComment() {
//        return comment;
//    }
//
//    public void setComment(String comment) {
//        this.comment = comment;
//    }
//}
