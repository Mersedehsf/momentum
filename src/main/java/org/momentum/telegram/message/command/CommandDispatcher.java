package org.momentum.telegram.message.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

//figures out whether it's /add, /list, etc.
@Component
public class CommandDispatcher {

    private final List<CommandHandler> handlers;

    public CommandDispatcher(List<CommandHandler> handlers) {
        this.handlers = handlers;
    }

    public void dispatch(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String text = update.getMessage().getText();

        for (CommandHandler handler : handlers) {
            if (handler.supports(text)) {
                handler.handle(update);
                return;
            }
        }
    }
}
