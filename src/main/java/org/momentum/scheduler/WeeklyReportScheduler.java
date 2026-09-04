package org.momentum.scheduler;

import org.momentum.dto.WeeklySummaryDTO;
import org.momentum.services.WeeklyAnalysisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class WeeklyReportScheduler {

    private final WeeklyAnalysisService weeklyAnalysisService;
    private final TelegramClient telegramClient;
    private final String chatId;

    public WeeklyReportScheduler(WeeklyAnalysisService weeklyAnalysisService, TelegramClient telegramClient, @Value("${telegram.chat-id}") String chatId) {
        this.weeklyAnalysisService = weeklyAnalysisService;
        this.telegramClient = telegramClient;
        this.chatId = chatId;
    }

    @Scheduled(cron = "0 55 23 * * FRI", zone = "Asia/Tehran")
    public void sendWeeklyReport() {

        WeeklySummaryDTO summary = weeklyAnalysisService.analyze();
        String text = weeklyAnalysisService.format(summary);

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
}
