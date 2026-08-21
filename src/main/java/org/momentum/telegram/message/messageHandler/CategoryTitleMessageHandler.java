package org.momentum.telegram.message.messageHandler;

import org.momentum.enums.ConversationState;
import org.momentum.models.task.Category;
import org.momentum.models.task.Task;
import org.momentum.services.CategoryService;
import org.momentum.services.TaskService;
import org.momentum.telegram.ConversationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class CategoryTitleMessageHandler implements MessageHandler {

    private final ConversationManager conversationManager;
    private final CategoryService categoryService;
    private final TelegramClient telegramClient;

    public CategoryTitleMessageHandler(ConversationManager conversationManager,CategoryService categoryService,TelegramClient telegramClient) {
        this.conversationManager = conversationManager;
        this.categoryService = categoryService;
        this.telegramClient = telegramClient;
    }

    @Override
    public boolean supports(Update update) {

        Long chatId = update.getMessage().getChatId();
        return conversationManager.getConversation(chatId).getState() == ConversationState.WAITING_FOR_CATEGORY_TITLE;
    }

    @Override
    public void handle(Update update) {

        String categoryTitle = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Category createdCategory = categoryService.create(categoryTitle);
        conversationManager.clear(chatId);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("✅ Category added!\n\n" + createdCategory.getTitle())
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
