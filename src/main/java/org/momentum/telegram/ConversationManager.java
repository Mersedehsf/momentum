package org.momentum.telegram;

import org.momentum.enums.ConversationState;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationManager {

    private final Map<Long, ConversationState> states = new ConcurrentHashMap<>();

    public void setState(Long chatId, ConversationState state) {
        states.put(chatId, state);
    }

    public ConversationState getState(Long chatId) {
        return states.getOrDefault(
                chatId,
                ConversationState.IDLE
        );
    }

    public void clearState(Long chatId) {
        states.remove(chatId);
    }
}
