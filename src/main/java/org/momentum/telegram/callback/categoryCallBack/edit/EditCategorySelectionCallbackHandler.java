package org.momentum.telegram.callback.categoryCallBack.edit;

import org.momentum.enums.ConversationState;
import org.momentum.models.task.Category;
import org.momentum.services.CategoryService;
import org.momentum.telegram.Conversation;
import org.momentum.telegram.ConversationManager;
import org.momentum.telegram.callback.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class EditCategorySelectionCallbackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final ConversationManager conversationManager;
    private final CategoryService categoryService;

    public EditCategorySelectionCallbackHandler(TelegramClient telegramClient, ConversationManager conversationManager, CategoryService categoryService) {
        this.telegramClient = telegramClient;
        this.conversationManager = conversationManager;
        this.categoryService = categoryService;
    }

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith("EDIT_CATEGORY:");
    }

    @Override
    public void handle(Update update) {

        String callbackData = update.getCallbackQuery().getData();

        Long objectId = Long.parseLong(callbackData.substring("EDIT_CATEGORY:".length()));

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        Conversation conversation = conversationManager.getConversation(chatId);

        conversation.setObjectId(objectId);
        conversation.setState(ConversationState.EDITING_CATEGORY);

        Category category = categoryService.findById(objectId);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(category.toString())
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}