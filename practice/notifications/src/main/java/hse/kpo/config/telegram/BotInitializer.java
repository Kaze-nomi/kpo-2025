package hse.kpo.config.telegram;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import hse.kpo.telegram.NotificationBot;

@Configuration
@RequiredArgsConstructor
public class BotInitializer {
    private final TelegramBotsApi telegramBotsApi;
    private final NotificationBot notificationBot;

    @PostConstruct
    public void init() {
        try {
            telegramBotsApi.registerBot(notificationBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to register bot", e);
        }
    }
}