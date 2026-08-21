package org.momentum.telegram.message.commandHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


@Component
public class StartCommandHandler implements CommandHandler {

    private final TelegramClient telegramClient;

    @Autowired
    public StartCommandHandler(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }


    @Override
    public boolean supports(String command) {
        return "/start".equals(command);
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getMessage().getChatId();

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("""
                         👋 Welcome to Momentum!
                        
                         Your personal productivity assistant 🍓.
                        
                         📋 What would you like to do?
                        """)
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
                keyboardRow(new InlineKeyboardRow(button("🫒 Edit task", "EDIT_TASK"))).
                keyboardRow(new InlineKeyboardRow(button("🥒 Add Category", "ADD_CATEGORY"))).
                keyboardRow(new InlineKeyboardRow(button("🍊 My Categories", "READ_CATEGORIES"))).
                keyboardRow(new InlineKeyboardRow(button("🍛 Edit Category", "EDIT_CATEGORY"))).
                keyboardRow(new InlineKeyboardRow(button("🎡 Daily Summary", "DAILY_SUMMARY"))).
                build();
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();

    }
}
