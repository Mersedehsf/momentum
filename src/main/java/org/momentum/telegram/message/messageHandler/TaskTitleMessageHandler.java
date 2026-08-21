package org.momentum.telegram.message.messageHandler;

import org.momentum.enums.ConversationState;
import org.momentum.models.task.Task;
import org.momentum.services.TaskService;
import org.momentum.telegram.ConversationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class TaskTitleMessageHandler implements MessageHandler {

    private final ConversationManager conversationManager;
    private final TaskService taskService;
    private final TelegramClient telegramClient;

    public TaskTitleMessageHandler(ConversationManager conversationManager,TaskService taskService,TelegramClient telegramClient) {
        this.conversationManager = conversationManager;
        this.taskService = taskService;
        this.telegramClient = telegramClient;
    }

    @Override
    public boolean supports(Update update) {

        Long chatId = update.getMessage().getChatId();
        return conversationManager.getConversation(chatId).getState() == ConversationState.WAITING_FOR_TASK_TITLE;
    }

    @Override
    public void handle(Update update) {

        String taskTitle = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Task createdTask = taskService.create(taskTitle);
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