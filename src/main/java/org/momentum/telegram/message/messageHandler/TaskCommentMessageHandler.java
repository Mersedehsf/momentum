package org.momentum.telegram.message.messageHandler;

import org.momentum.enums.ConversationState;
import org.momentum.models.task.Category;
import org.momentum.models.task.Task;
import org.momentum.services.CategoryService;
import org.momentum.services.TaskService;
import org.momentum.telegram.Conversation;
import org.momentum.telegram.ConversationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class TaskCommentMessageHandler implements MessageHandler {

    private final ConversationManager conversationManager;
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final TelegramClient telegramClient;

    public TaskCommentMessageHandler(ConversationManager conversationManager, TaskService taskService, CategoryService categoryService, TelegramClient telegramClient) {
        this.conversationManager = conversationManager;
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.telegramClient = telegramClient;
    }

    @Override
    public boolean supports(Update update) {

        Long chatId = update.getMessage().getChatId();
        return conversationManager.getConversation(chatId).getState() == ConversationState.WAITING_FOR_TASK_COMMENT;
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        Conversation conversation = conversationManager.getConversation(chatId);

        String comment = "-".equals(text.trim()) ? null : text;
        String title = conversation.getTaskTitle();
        Integer estimatedMinutes = conversation.getTaskEstimatedMinutes();

        Category category = null;
        if (conversation.getTaskCategoryId() != null) {
            category = categoryService.findById(conversation.getTaskCategoryId());
        }

        Task createdTask = taskService.createTask(title, estimatedMinutes, category, comment);

        conversationManager.clear(chatId);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("✅ Task added!\n\n" + createdTask.getTitle())
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
