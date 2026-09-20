package org.momentum.telegram.callback.taskCallBacks;

import org.momentum.enums.ConversationState;
import org.momentum.models.task.Category;
import org.momentum.models.task.Task;
import org.momentum.services.CategoryService;
import org.momentum.services.TaskService;
import org.momentum.telegram.Conversation;
import org.momentum.telegram.ConversationManager;
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
public class AddTaskCategoryCallbackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final ConversationManager conversationManager;
    private final TaskService taskService;
    private final CategoryService categoryService;

    public AddTaskCategoryCallbackHandler(TelegramClient telegramClient, ConversationManager conversationManager,TaskService taskService,CategoryService categoryService) {
        this.telegramClient = telegramClient;
        this.conversationManager = conversationManager;
        this.taskService = taskService;
        this.categoryService = categoryService;
    }

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith("SELECT_TASK_CATEGORY:");
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        String callbackData = update.getCallbackQuery().getData();

        String categoryValue = callbackData.substring("SELECT_TASK_CATEGORY:".length());

        Conversation conversation = conversationManager.getConversation(chatId);

        if (!"skip".equals(categoryValue)) {
            conversation.setTaskCategoryId(Long.parseLong(categoryValue));
        } else {
            conversation.setTaskCategoryId(null);
        }

        String title = conversation.getTaskTitle();
        Integer estimatedMinutes = conversation.getTaskEstimatedMinutes();

        Category category = null;
        if (conversation.getTaskCategoryId() != null) {
            category = categoryService.findById(conversation.getTaskCategoryId());
        }

        Task createdTask = taskService.createTask(title, estimatedMinutes, category);

        conversationManager.clear(chatId);

        List<InlineKeyboardRow> rows = new ArrayList<>();

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text("🏠 Main Menu")
                        .callbackData("MAIN_MENU")
                        .build()));

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("✅ Task added!\n\n" + createdTask.getTitle())
                .replyMarkup(new InlineKeyboardMarkup(rows))
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
