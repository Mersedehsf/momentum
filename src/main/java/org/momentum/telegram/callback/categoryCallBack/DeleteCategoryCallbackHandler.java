package org.momentum.telegram.callback.categoryCallBack;

import org.momentum.dto.CategoryDTO;
import org.momentum.services.CategoryService;
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
public class DeleteCategoryCallbackHandler implements CallbackHandler {

    private static final String ENTRY = "DELETE_CATEGORY";
    private static final String SELECT_PREFIX = "DELETE_CATEGORY:";
    private static final String CONFIRM_PREFIX = "CONFIRM_DELETE_CATEGORY:";
    private static final String CANCEL = "CANCEL_DELETE_CATEGORY";

    private final TelegramClient telegramClient;
    private final CategoryService categoryService;

    public DeleteCategoryCallbackHandler(TelegramClient telegramClient, CategoryService categoryService) {
        this.telegramClient = telegramClient;
        this.categoryService = categoryService;
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
            showCategorySelection(chatId);
        } else if (callbackData.startsWith(SELECT_PREFIX)) {
            Long categoryId = Long.parseLong(callbackData.substring(SELECT_PREFIX.length()));
            showConfirmation(chatId, categoryId);
        } else if (callbackData.startsWith(CONFIRM_PREFIX)) {
            Long categoryId = Long.parseLong(callbackData.substring(CONFIRM_PREFIX.length()));
            confirmDelete(chatId, categoryId);
        } else if (CANCEL.equals(callbackData)) {
            sendMessage(chatId, "🗑️ Deletion cancelled.");
        }
    }

    private void showCategorySelection(Long chatId) {

        List<CategoryDTO> categories = categoryService.findAll();

        if (categories.isEmpty()) {
            sendMessage(chatId, "📋 You don't have any categories to delete.");
            return;
        }

        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (CategoryDTO category : categories) {

            InlineKeyboardButton button =
                    InlineKeyboardButton.builder()
                            .text("🗑️ " + category.getTitle())
                            .callbackData(SELECT_PREFIX + category.getId())
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
                "🗑️ Which category would you like to delete?",
                InlineKeyboardMarkup.builder().keyboard(rows).build()
        );
    }

    private void showConfirmation(Long chatId, Long categoryId) {

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                                .text("✅ Yes, delete")
                                .callbackData(CONFIRM_PREFIX + categoryId)
                                .build()
                ))
                .keyboardRow(new InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                                .text("❌ Cancel")
                                .callbackData(CANCEL)
                                .build()
                ))
                .build();

        sendMessage(chatId, "Are you sure you want to delete this category?", keyboard);
    }

    private void confirmDelete(Long chatId, Long categoryId) {

        categoryService.softDelete(categoryId);
        sendMessage(chatId, "🗑️ Category deleted!");
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
