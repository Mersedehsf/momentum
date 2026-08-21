package org.momentum.telegram.callback.taskCallBacks;

import org.momentum.dto.TaskDTO;
import org.momentum.services.TaskService;
import org.momentum.telegram.callback.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

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

        String callbackData = update.getCallbackQuery().getData();

        Long taskId = Long.parseLong(callbackData.substring("COMPLETE_TASK:".length()));

        taskService.completeTask(taskId);

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        Integer messageId = update.getCallbackQuery()
                .getMessage()
                .getMessageId();

        List<TaskDTO> tasks = taskService.findTodaysTasks();

        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("📋 Today's Tasks")
                .replyMarkup(buildTasksKeyboardAfterModify(tasks))
                .build();

        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        sendConfirmation(chatId);
    }

    private InlineKeyboardMarkup buildTasksKeyboardAfterModify(List<TaskDTO> tasks) {

        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (TaskDTO task : tasks) {

            InlineKeyboardButton button =
                    InlineKeyboardButton.builder()
                            .text("⬜ " + task.getTitle())
                            .callbackData("COMPLETE_TASK:" + task.getId())
                            .build();

            rows.add(new InlineKeyboardRow(button));
        }

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text("🚀 Main Menu")
                        .callbackData("MAIN_MENU")
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();

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
