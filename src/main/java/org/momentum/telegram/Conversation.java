package org.momentum.telegram;

import org.momentum.enums.ConversationState;

public class Conversation {

    private ConversationState state;
    private Long taskId;

    public ConversationState getState() {
        return state;
    }

    public void setState(ConversationState state) {
        this.state = state;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
