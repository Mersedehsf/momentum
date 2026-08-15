package org.momentum.telegram.callback;

import org.momentum.services.TaskService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class CompleteTaskCallbackHandler implements CallbackHandler {

    private final TaskService taskService;
    private final TelegramClient telegramClient;

    public CompleteTaskCallbackHandler(TaskService taskService, TelegramClient telegramClient) {
        this.taskService = taskService;
        this.telegramClient = telegramClient;
    }

    @Override
    public boolean supports(String callbackData) {

        return callbackData.startsWith("COMPLETE_TASK:");
    }

    @Override
    public void handle(Update update) {

        String callbackData =
                update.getCallbackQuery().getData();

        Long taskId = Long.parseLong(
                callbackData.substring("COMPLETE_TASK:".length())
        );

        taskService.completeTask(taskId);

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        sendConfirmation(chatId);
    }

    private void sendConfirmation(Long chatId) {

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("✅ Task completed!")
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}