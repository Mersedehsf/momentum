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

import java.util.Objects;

@Component
public class EditTaskMessageHandler implements MessageHandler {

    private final ConversationManager conversationManager;
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final TelegramClient telegramClient;

    public EditTaskMessageHandler(ConversationManager conversationManager, TaskService taskService, CategoryService categoryService, TelegramClient telegramClient) {
        this.conversationManager = conversationManager;
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.telegramClient = telegramClient;
    }

    @Override
    public boolean supports(Update update) {
        Long chatId = update.getMessage().getChatId();
        return conversationManager.getConversation(chatId).getState() == ConversationState.EDITING_TASK;
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getMessage().getChatId();
        Task foundedTask = taskService.findById(conversationManager.getConversation(chatId).getObjectId());
        editTask(chatId, update.getMessage().getText(), foundedTask);

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

    private void editTask(Long chatId, String text, Task task) {

        String title = extractValue(text, "title:");
        String estimatedMinutes = extractValue(text, "estimatedMinutes:");
        String category = extractValue(text, "category:");
        String comment = extractValue(text, "comment:");
        Integer completed = extractValue(text, "completed:") == null ? null : Integer.valueOf(Objects.requireNonNull(extractValue(text, "comment:")));

        Category foundedCategory = categoryService.findByTitle(category);

        Task updatedTask = new Task(title, Integer.getInteger(estimatedMinutes), foundedCategory, comment, completed);

        taskService.updateTask(task.getId(), updatedTask);

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
