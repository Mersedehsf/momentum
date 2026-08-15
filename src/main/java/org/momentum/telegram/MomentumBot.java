package org.momentum.telegram;

import org.momentum.telegram.callback.CallbackDispatcher;
import org.momentum.telegram.command.CommandDispatcher;
import org.momentum.telegram.message.MessageDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MomentumBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;
    private final CommandDispatcher commandDispatcher;
    private final CallbackDispatcher callbackDispatcher;
    private final MessageDispatcher messageDispatcher;

    public MomentumBot(@Value("${telegram.bot.token}") String botToken, CommandDispatcher commandDispatcher, CallbackDispatcher callbackDispatcher, MessageDispatcher messageDispatcher) {
        this.botToken = botToken;
        this.commandDispatcher = commandDispatcher;
        this.callbackDispatcher = callbackDispatcher;
        this.messageDispatcher = messageDispatcher;
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
            messageDispatcher.dispatch(update);
        }
    }
}