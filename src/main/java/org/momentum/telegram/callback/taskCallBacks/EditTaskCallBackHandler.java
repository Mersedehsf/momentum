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
public class EditTaskCallBackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final TaskService taskService;

    public EditTaskCallBackHandler(TelegramClient telegramClient, TaskService taskService) {
        this.telegramClient = telegramClient;
        this.taskService = taskService;
    }

    @Override
    public boolean supports(String callbackData) {
        return "EDIT_TASK".equals(callbackData);
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        List<TaskDTO> tasks = taskService.findTodaysTasks();

        if (tasks.isEmpty()) {

            sendMessage(
                    chatId,
                    "📝 You don't have any tasks to edit today."
            );

            return;
        }

        sendTaskSelection(chatId, tasks);
    }

    private void sendTaskSelection(Long chatId, List<TaskDTO> tasks) {

        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (TaskDTO task : tasks) {

            InlineKeyboardButton button =
                    InlineKeyboardButton.builder()
                            .text("📝 " + task.getTitle())
                            .callbackData("EDIT_TASK:" + task.getId())
                            .build();

            rows.add(new InlineKeyboardRow(button));
        }

        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("🏠 Main Menu")
                        .callbackData("MAIN_MENU")
                        .build()
        ));

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("🫒 Which task would you like to edit?")
                .replyMarkup(InlineKeyboardMarkup.builder()
                                .keyboard(rows)
                                .build())
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Long chatId, String text) {

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}