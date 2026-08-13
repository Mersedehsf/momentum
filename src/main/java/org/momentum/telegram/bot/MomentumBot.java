package org.momentum.telegram.bot;

import org.momentum.telegram.command.CallbackDispatcher;
import org.momentum.telegram.command.CommandDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class MomentumBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final CommandDispatcher commandDispatcher;
    private final CallbackDispatcher callbackDispatcher;
    private final String botToken;

    public MomentumBot(@Value("${telegram.bot.token}") String botToken, CommandDispatcher commandDispatcher, CallbackDispatcher callbackDispatcher) {
        this.botToken = botToken;
        this.commandDispatcher = commandDispatcher;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasCallbackQuery()) {
            callbackDispatcher.dispatch(update);
            return;
        }

        if (update.hasMessage()) {
            commandDispatcher.dispatch(update);
        }
    }
}