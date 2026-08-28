package org.momentum.telegram;

import org.momentum.enums.ConversationState;

public class Conversation {

    private ConversationState state;
    private Long objectId;
    private String taskTitle;
    private Integer taskEstimatedMinutes;
    private Long taskCategoryId;

    public ConversationState getState() {
        return state;
    }

    public void setState(ConversationState state) {
        this.state = state;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public Integer getTaskEstimatedMinutes() {
        return taskEstimatedMinutes;
    }

    public void setTaskEstimatedMinutes(Integer taskEstimatedMinutes) {
        this.taskEstimatedMinutes = taskEstimatedMinutes;
    }

    public Long getTaskCategoryId() {
        return taskCategoryId;
    }

    public void setTaskCategoryId(Long taskCategoryId) {
        this.taskCategoryId = taskCategoryId;
    }
}
