package org.momentum.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.momentum.dto.WeeklySummaryDTO;
import org.momentum.services.WeeklyAnalysisService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeklyReportSchedulerTest {

    @Mock
    private WeeklyAnalysisService weeklyAnalysisService;

    @Mock
    private TelegramClient telegramClient;

    private WeeklyReportScheduler scheduler;

    private static final String CHAT_ID = "123456789";

    @BeforeEach
    void setUp() {
        scheduler = new WeeklyReportScheduler(weeklyAnalysisService, telegramClient, CHAT_ID);
    }

    @Test
    void sendWeeklyReport_callsAnalyzeAndFormatAndSendsMessage() throws TelegramApiException {
        WeeklySummaryDTO summary = new WeeklySummaryDTO();
        String formattedText = "Weekly Report:\nTotal: 5";

        when(weeklyAnalysisService.analyze()).thenReturn(summary);
        when(weeklyAnalysisService.format(summary)).thenReturn(formattedText);

        scheduler.sendWeeklyReport();

        verify(weeklyAnalysisService).analyze();
        verify(weeklyAnalysisService).format(summary);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient).execute(messageCaptor.capture());

        SendMessage sentMessage = messageCaptor.getValue();
        assertEquals(CHAT_ID, sentMessage.getChatId());
        assertEquals(formattedText, sentMessage.getText());
    }

    @Test
    void sendWeeklyReport_wrapsTelegramApiExceptionInRuntimeException() throws TelegramApiException {
        WeeklySummaryDTO summary = new WeeklySummaryDTO();
        when(weeklyAnalysisService.analyze()).thenReturn(summary);
        when(weeklyAnalysisService.format(summary)).thenReturn("text");
        when(telegramClient.execute(org.mockito.ArgumentMatchers.any(SendMessage.class)))
                .thenThrow(new TelegramApiException("API error"));

        try {
            scheduler.sendWeeklyReport();
            throw new AssertionError("Expected RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("API error", e.getCause().getMessage());
        }
    }
}