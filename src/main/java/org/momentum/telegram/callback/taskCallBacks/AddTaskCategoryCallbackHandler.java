package org.momentum.telegram.callback.taskCallBacks;

import org.momentum.enums.ConversationState;
import org.momentum.telegram.Conversation;
import org.momentum.telegram.ConversationManager;
import org.momentum.telegram.callback.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class AddTaskCategoryCallbackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final ConversationManager conversationManager;

    public AddTaskCategoryCallbackHandler(TelegramClient telegramClient, ConversationManager conversationManager) {
        this.telegramClient = telegramClient;
        this.conversationManager = conversationManager;
    }

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith("SELECT_TASK_CATEGORY:");
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        String callbackData = update.getCallbackQuery().getData();

        String categoryValue = callbackData.substring("SELECT_TASK_CATEGORY:".length());

        Conversation conversation = conversationManager.getConversation(chatId);

        if (!"skip".equals(categoryValue)) {
            conversation.setTaskCategoryId(Long.parseLong(categoryValue));
        } else {
            conversation.setTaskCategoryId(null);
        }

        conversation.setState(ConversationState.WAITING_FOR_TASK_COMMENT);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("💬 Add a comment? (type your comment, or send `-` to skip)")
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
