package org.momentum.telegram.callback.categoryCallBack.edit;

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
public class EditCategoryCallBackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final CategoryService categoryService;

    public EditCategoryCallBackHandler(TelegramClient telegramClient, CategoryService categoryService) {
        this.telegramClient = telegramClient;
        this.categoryService = categoryService;
    }

    @Override
    public boolean supports(String callbackData) {
        return "EDIT_CATEGORY".equals(callbackData);
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        List<CategoryDTO> categories = categoryService.findAll();

        if (categories.isEmpty()) {

            sendMessage(
                    chatId,
                    "📝 You have not added any categories yet."
            );

            return;
        }

        sendCategorySelection(chatId, categories);
    }

    private void sendCategorySelection(Long chatId, List<CategoryDTO> categories) {

        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (CategoryDTO category : categories) {

            InlineKeyboardButton button =
                    InlineKeyboardButton.builder()
                            .text("📝 " + category.getTitle())
                            .callbackData("EDIT_CATEGORY:" + category.getId())
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
                .text("🫒 Which category would you like to edit?")
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