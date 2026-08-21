package org.momentum.telegram.callback.taskCallBacks;

import org.momentum.dto.TaskDTO;
import org.momentum.enums.ConversationState;
import org.momentum.models.task.Task;
import org.momentum.services.TaskService;
import org.momentum.telegram.Conversation;
import org.momentum.telegram.ConversationManager;
import org.momentum.telegram.callback.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class EditTaskSelectionCallbackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final ConversationManager conversationManager;
    private final TaskService taskService;

    public EditTaskSelectionCallbackHandler(TelegramClient telegramClient, ConversationManager conversationManager,TaskService taskService) {
        this.telegramClient = telegramClient;
        this.conversationManager = conversationManager;
        this.taskService = taskService;
    }

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith("EDIT_TASK:");
    }

    @Override
    public void handle(Update update) {

        String callbackData = update.getCallbackQuery().getData();

        Long taskId = Long.parseLong(callbackData.substring("EDIT_TASK:".length()));

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        Conversation conversation = conversationManager.getConversation(chatId);

        conversation.setTaskId(taskId);
        conversation.setState(ConversationState.EDITING_TASK);

        Task task = taskService.findById(taskId);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(task.toString())
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}