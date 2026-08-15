package org.momentum.telegram.message;

import org.momentum.telegram.command.CommandDispatcher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class MessageDispatcher {

    private final CommandDispatcher commandDispatcher;
    private final List<MessageHandler> handlers;

    public MessageDispatcher(CommandDispatcher commandDispatcher,List<MessageHandler> handlers) {
        this.commandDispatcher = commandDispatcher;
        this.handlers = handlers;
    }

    public void dispatch(Update update) {
        String text = update.getMessage().getText();

        if (text.startsWith("/")) {
            commandDispatcher.dispatch(update);
            return;
        }
        for (MessageHandler handler : handlers) {

            if (handler.supports(update)) {
                handler.handle(update);
                return;
            }
        }
    }
}
