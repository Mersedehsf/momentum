package org.momentum.telegram.callback.categoryCallBack;

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
public class AddCategoryCallBackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final ConversationManager conversationManager;

    public AddCategoryCallBackHandler(TelegramClient telegramClient, ConversationManager conversationManager) {
        this.telegramClient = telegramClient;
        this.conversationManager = conversationManager;
    }

    @Override
    public boolean supports(String callbackData) {
        return "ADD_CATEGORY".equals(callbackData);
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        Conversation conversation = conversationManager.getConversation(chatId);
        conversation.setState(ConversationState.WAITING_FOR_CATEGORY_TITLE);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("✏️ What category would you like to add?")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
