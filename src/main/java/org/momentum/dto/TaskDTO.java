package org.momentum.dto;

public class TaskDTO extends BaseDTO {

    private String title;

    private String categoryTitle;

    private String comment;

    private Integer completed;

    public TaskDTO(Long id,String title, String categoryTitle, String comment, Integer completed) {
        super(id);
        this.title = title;
        this.categoryTitle = categoryTitle;
        this.comment = comment;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
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
}
