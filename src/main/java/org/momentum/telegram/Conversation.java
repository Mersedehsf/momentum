package org.momentum.telegram;

import org.momentum.enums.ConversationState;

public class Conversation {

    private ConversationState state;
    private Long objectId;

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
}
