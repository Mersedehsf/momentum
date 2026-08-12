package org.momentum.telegram.command;

import org.telegram.telegrambots.meta.api.objects.Update;

//calls your services.
public interface CommandHandler {

    boolean supports(String command);

    void handle(Update update);
}
