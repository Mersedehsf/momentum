package org.momentum.telegram.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class CallbackDispatcher {

    private final TelegramClient telegramClient;

    private final List<CallbackHandler> handlers ;

    public CallbackDispatcher(TelegramClient telegramClient, List<CallbackHandler> handlers) {
        this.telegramClient = telegramClient;
        this.handlers = handlers;
    }

    public void dispatch(Update update) {

        String callbackData = update.getCallbackQuery().getData();

        answerCallback(update);

        for (CallbackHandler handler : handlers) {

            if (handler.supports(callbackData)) {
                handler.handle(update);
                return;
            }
        }
    }

    private void answerCallback(Update update) {

        AnswerCallbackQuery answer =
                AnswerCallbackQuery.builder()
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .build();
        try {
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
