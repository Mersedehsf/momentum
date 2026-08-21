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
public class MyTasksCallBackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final TaskService taskService;


    public MyTasksCallBackHandler(TelegramClient telegramClient, TaskService taskService) {
        this.telegramClient = telegramClient;
        this.taskService = taskService;
    }

    @Override
    public boolean supports(String callbackData) {
        return "READ_TASK".equals(callbackData);
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        List<TaskDTO> tasks = taskService.findTodaysTasks();

        String text = buildTaskMessage(tasks);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(buildTasksKeyboard(tasks))
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildTaskMessage(List<TaskDTO> tasks) {

        if (tasks.isEmpty()) {
            return "📋 You haven't added any tasks today.";
        }

        StringBuilder message = new StringBuilder();

        message.append("📋 Today's Tasks\n\n");

        for (int i = 0; i < tasks.size(); i++) {
            message.append("🌱")
                    .append(". ")
                    .append(tasks.get(i).getTitle())
                    .append(tasks.get(i).getCompleted() == 1 ? "  ✅" : "")
                    .append("\n");
        }

        return message.toString();
    }

    private InlineKeyboardMarkup buildTasksKeyboard(List<TaskDTO> tasks) {

        List<TaskDTO> notCompletedTasks = tasks.stream().filter(taskDTO -> taskDTO.getCompleted() ==0).toList();

        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (TaskDTO task : notCompletedTasks) {

            InlineKeyboardButton button =
                    InlineKeyboardButton.builder()
                            .text("⬜ " + task.getTitle())
                            .callbackData("COMPLETE_TASK:" + task.getId())
                            .build();

            rows.add(new InlineKeyboardRow(button));
        }

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text("🏠 Main Menu")
                        .callbackData("MAIN_MENU")
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
