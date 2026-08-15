package org.momentum.telegram.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class MainMenuCallbackHandler implements CallbackHandler{

    private final TelegramClient telegramClient;

    public MainMenuCallbackHandler(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public boolean supports(String callbackData) {
        return "MAIN_MENU".equals(callbackData);
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("📋 What would you like to do?")
                .replyMarkup(keyboard())
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InlineKeyboardMarkup keyboard() {

        return InlineKeyboardMarkup.builder().
        keyboardRow(new InlineKeyboardRow(button("➕ Add task", "ADD_TASK"))).
                keyboardRow(new InlineKeyboardRow(button("🗒️ My tasks", "READ_TASK"))).
                keyboardRow(new InlineKeyboardRow(button("➕ Update task", "UPDATE_TASK"))).
                keyboardRow(new InlineKeyboardRow(button("➕ Delete task", "DELETE_TASK"))).
                build();
    }

    private InlineKeyboardButton button(String text, String callbackData) {

        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}
