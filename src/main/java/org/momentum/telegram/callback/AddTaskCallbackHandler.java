package org.momentum.telegram.callback;

import org.momentum.enums.ConversationState;
import org.momentum.telegram.ConversationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class AddTaskCallbackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final ConversationManager conversationManager;

    public AddTaskCallbackHandler(TelegramClient telegramClient, ConversationManager conversationManager) {
        this.telegramClient = telegramClient;
        this.conversationManager = conversationManager;
    }

    @Override
    public boolean supports(String callbackData) {
        return "ADD_TASK".equals(callbackData);
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        conversationManager.setState(chatId, ConversationState.WAITING_FOR_TASK_TITLE);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("✏️ What task would you like to add?")
                .build();//todo then ask,in what category
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
