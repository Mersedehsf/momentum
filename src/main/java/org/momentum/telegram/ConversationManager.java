package org.momentum.telegram;

import org.momentum.telegram.Conversation;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationManager {

    private final Map<Long, Conversation> conversations = new ConcurrentHashMap<>();

    public Conversation getConversation(Long chatId) {
        return conversations.computeIfAbsent(
                chatId,
                id -> new Conversation()
        );
    }

    public void clear(Long chatId) {
        conversations.remove(chatId);
    }
}