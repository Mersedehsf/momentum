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
public class EditCategoryMessageHandler implements MessageHandler {

    private final ConversationManager conversationManager;
    private final CategoryService categoryService;
    private final TelegramClient telegramClient;

    public EditCategoryMessageHandler(ConversationManager conversationManager, CategoryService categoryService, TelegramClient telegramClient) {
        this.conversationManager = conversationManager;
        this.categoryService = categoryService;
        this.telegramClient = telegramClient;
    }

    @Override
    public boolean supports(Update update) {
        Long chatId = update.getMessage().getChatId();
        return conversationManager.getConversation(chatId).getState() == ConversationState.EDITING_CATEGORY;
    }

    @Override
    public void handle(Update update)  {

        Long chatId = update.getMessage().getChatId();
        Category foundedCategory = categoryService.findById(conversationManager.getConversation(chatId).getObjectId());
        editCategory(chatId, update.getMessage().getText(), foundedCategory);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("Done!")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editCategory(Long chatId, String text, Category category) {

        String title = extractValue(text, "title:");
        String deleted = extractValue(text, "deleted:");

        Category newCategory = new Category(title,Integer.valueOf(deleted));

        categoryService.updateCategory(category.getId(), newCategory);

        conversationManager.clear(chatId);
    }

    private String extractValue(String text, String field) {

        for (String line : text.split("\n")) {

            if (line.startsWith(field)) {
                return line.substring(field.length()).trim();
            }
        }
        return null;
    }
}
