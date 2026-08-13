package org.momentum.dto;

public class TaskDTO extends BaseDTO {

    private String title;

    private String categoryTitle;

    private String comment;

    public TaskDTO(String title, String categoryTitle, String comment) {
        this.title = title;
        this.categoryTitle = categoryTitle;
        this.comment = comment;
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
}
