package org.momentum.telegram.message.messageHandler;

import org.momentum.dto.CategoryDTO;
import org.momentum.enums.ConversationState;
import org.momentum.services.CategoryService;
import org.momentum.telegram.Conversation;
import org.momentum.telegram.ConversationManager;
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
public class TaskEstimatedMinutesMessageHandler implements MessageHandler {

    private final ConversationManager conversationManager;
    private final CategoryService categoryService;
    private final TelegramClient telegramClient;

    public TaskEstimatedMinutesMessageHandler(ConversationManager conversationManager, CategoryService categoryService, TelegramClient telegramClient) {
        this.conversationManager = conversationManager;
        this.categoryService = categoryService;
        this.telegramClient = telegramClient;
    }

    @Override
    public boolean supports(Update update) {

        Long chatId = update.getMessage().getChatId();
        return conversationManager.getConversation(chatId).getState() == ConversationState.WAITING_FOR_TASK_ESTIMATED_MINUTES;
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        Integer minutes;
        try {
            minutes = Integer.valueOf(text.trim());
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Please enter a valid number for estimated minutes.");
            return;
        }

        Conversation conversation = conversationManager.getConversation(chatId);
        conversation.setTaskEstimatedMinutes(minutes);
        conversation.setState(ConversationState.WAITING_FOR_TASK_CATEGORY);

        sendCategorySelection(chatId);
    }

    private void sendCategorySelection(Long chatId) {

        List<CategoryDTO> categories = categoryService.findAll();

        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (CategoryDTO category : categories) {

            InlineKeyboardButton button =
                    InlineKeyboardButton.builder()
                            .text(category.getTitle())
                            .callbackData("SELECT_TASK_CATEGORY:" + category.getId())
                            .build();

            rows.add(new InlineKeyboardRow(button));
        }

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text("🚫 No category / Skip")
                        .callbackData("SELECT_TASK_CATEGORY:skip")
                        .build()
        ));

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("🗂️ Which category does this task belong to?")
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
