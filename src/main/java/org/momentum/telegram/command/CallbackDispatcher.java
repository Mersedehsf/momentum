package org.momentum.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class CallbackDispatcher {

    private final TelegramClient telegramClient;

    public CallbackDispatcher(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public void dispatch(Update update) {

        String callbackData =
                update.getCallbackQuery().getData();

        System.out.println("Callback received: " + callbackData);

        AnswerCallbackQuery answerCallbackQuery =
                AnswerCallbackQuery.builder()
                        .callbackQueryId(
                                update.getCallbackQuery().getId()
                        )
                        .build();

        try {
            telegramClient.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
