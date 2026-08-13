package org.momentum.telegram.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
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

                        Use /menu to see available commands.
                        """)
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
