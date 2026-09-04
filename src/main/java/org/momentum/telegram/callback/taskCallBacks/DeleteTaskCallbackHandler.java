package org.momentum.telegram.callback.taskCallBacks;

import org.momentum.dto.TaskDTO;
import org.momentum.services.TaskService;
import org.momentum.telegram.callback.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeleteTaskCallbackHandler implements CallbackHandler {

    private static final String ENTRY = "DELETE_TASK";
    private static final String SELECT_PREFIX = "DELETE_TASK:";
    private static final String CONFIRM_PREFIX = "CONFIRM_DELETE_TASK:";
    private static final String CANCEL = "CANCEL_DELETE_TASK";

    private final TelegramClient telegramClient;
    private final TaskService taskService;

    public DeleteTaskCallbackHandler(TelegramClient telegramClient, TaskService taskService) {
        this.telegramClient = telegramClient;
        this.taskService = taskService;
    }

    @Override
    public boolean supports(String callbackData) {
        return ENTRY.equals(callbackData)
                || callbackData.startsWith(SELECT_PREFIX)
                || callbackData.startsWith(CONFIRM_PREFIX)
                || CANCEL.equals(callbackData);
    }

    @Override
    public void handle(Update update) {

        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (ENTRY.equals(callbackData)) {
            showTaskSelection(chatId);
        } else if (callbackData.startsWith(SELECT_PREFIX)) {
            Long taskId = Long.parseLong(callbackData.substring(SELECT_PREFIX.length()));
            showConfirmation(chatId, taskId);
        } else if (callbackData.startsWith(CONFIRM_PREFIX)) {
            Long taskId = Long.parseLong(callbackData.substring(CONFIRM_PREFIX.length()));
            confirmDelete(chatId, taskId);
        } else if (CANCEL.equals(callbackData)) {
            sendMessage(chatId, "🗑️ Deletion cancelled.");
        }
    }

    private void showTaskSelection(Long chatId) {

        List<TaskDTO> tasks = taskService.findTodaysTasks();

        if (tasks.isEmpty()) {
            sendMessage(chatId, "📋 You don't have any tasks to delete today.");
            return;
        }

        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (TaskDTO task : tasks) {

            InlineKeyboardButton button =
                    InlineKeyboardButton.builder()
                            .text("🗑️ " + task.getTitle())
                            .callbackData(SELECT_PREFIX + task.getId())
                            .build();

            rows.add(new InlineKeyboardRow(button));
        }

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text("🏠 Main Menu")
                        .callbackData("MAIN_MENU")
                        .build()
        ));

        sendMessage(
                chatId,
                "🗑️ Which task would you like to delete?",
                InlineKeyboardMarkup.builder().keyboard(rows).build()
        );
    }

    private void showConfirmation(Long chatId, Long taskId) {

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                                .text("✅ Yes, delete")
                                .callbackData(CONFIRM_PREFIX + taskId)
                                .build()
                ))
                .keyboardRow(new InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                                .text("❌ Cancel")
                                .callbackData(CANCEL)
                                .build()
                ))
                .build();

        sendMessage(chatId, "Are you sure you want to delete this task?", keyboard);
    }

    private void confirmDelete(Long chatId, Long taskId) {

        taskService.softDelete(taskId);
        sendMessage(chatId, "🗑️ Task deleted!");
    }

    private void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    private void sendMessage(Long chatId, String text, InlineKeyboardMarkup replyMarkup) {

        SendMessage.SendMessageBuilder<?, ?> builder = SendMessage.builder().chatId(chatId).text(text);

        if (replyMarkup != null) {
            builder.replyMarkup(replyMarkup);
        }

        try {
            telegramClient.execute(builder.build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
