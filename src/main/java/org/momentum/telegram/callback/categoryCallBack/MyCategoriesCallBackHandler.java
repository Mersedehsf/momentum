package org.momentum.telegram.callback.categoryCallBack;

import org.momentum.dto.CategoryDTO;
import org.momentum.services.CategoryService;
import org.momentum.telegram.callback.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class MyCategoriesCallBackHandler implements CallbackHandler {

    private final TelegramClient telegramClient;
    private final CategoryService categoryService;


    public MyCategoriesCallBackHandler(TelegramClient telegramClient, CategoryService categoryService) {
        this.telegramClient = telegramClient;
        this.categoryService = categoryService;
    }

    @Override
    public boolean supports(String callbackData) {
        return "READ_CATEGORIES".equals(callbackData);
    }

    @Override
    public void handle(Update update) {

        Long chatId = update.getCallbackQuery()
                .getMessage()
                .getChatId();

        List<CategoryDTO> tasks = categoryService.findAll();

        String text = buildCategoriesMessage(tasks);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildCategoriesMessage(List<CategoryDTO> categories) {

        if (categories.isEmpty()) {
            return "📋 You haven't added any categories yet.";
        }

        StringBuilder message = new StringBuilder();

        message.append("📋 Your Categories\n\n");

        for (int i = 0; i < categories.size(); i++) {
            message.append("" + (i+1))
                    .append(". ")
                    .append(categories.get(i).getTitle())
                    .append("\n");
        }

        return message.toString();
    }
}
