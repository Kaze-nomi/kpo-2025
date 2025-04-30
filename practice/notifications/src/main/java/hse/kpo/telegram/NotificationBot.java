package hse.kpo.telegram;

import hse.kpo.config.telegram.TelegramConfig;
import hse.kpo.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class NotificationBot extends TelegramLongPollingBot {
    private final TelegramConfig config;
    private final SubscriptionService subscriptionService;

    public NotificationBot(TelegramConfig config, SubscriptionService subscriptionService) {
        super(config.getToken());
        this.config = config;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().isUserMessage() || update.getMessage().isGroupMessage() && update.getMessage().getText().toLowerCase().contains("бот")) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            try {
                String username = update.getMessage().getFrom().getUserName();
                Files.writeString(Paths.get("messages.txt"), String.format("%s <%s>: %s\n", username, chatId, messageText), StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.error("Failed to write message to file", e);
            }

            int random = (int) (Math.random() * 100);
            String response;

            switch (random) {
                case 0 -> response = "Хм, что же ты хотел этим сказать...";
                case 1 -> response = "Сильно";
                case 2 -> response = "Я тебя понял";
                case 3 -> response = "Мяу";
                case 4 -> response = "Gleb moment";
                case 5 -> response = "Интересно!";
                case 6 -> response = "Ага!";
                case 7 -> response = "Что-то новенькое";
                case 8 -> response = "Любопытно";
                case 9 -> response = "Ну-ну";
                case 10 -> response = "Это неожиданно";
                case 11 -> response = "Вот это да!";
                case 12 -> response = "Согласен";
                case 13 -> response = "Не думаю так";
                case 14 -> response = "Хороший вопрос";
                case 15 -> response = "Понятно";
                case 16 -> response = "Интересное замечание";
                case 17 -> response = "Это точно";
                case 18 -> response = "Скорее всего";
                case 19 -> response = "Не уверен";
                case 20 -> response = "Давай попробуем";
                case 21 -> response = "Возможно";
                case 22 -> response = "Нужно подумать";
                case 23 -> response = "Пока не ясно";
                case 24 -> response = "Хорошая идея!";
                case 25 -> response = "Это важно";
                case 26 -> response = "Неплохо!";
                case 27 -> response = "Звучит интересно";
                case 28 -> response = "Так и есть";
                case 29 -> response = "Да ладно!";
                case 30 -> response = "Ого!";
                case 31 -> response = "Это что-то новенькое";
                case 32 -> response = "Я так не думаю";
                case 33 -> response = "Сложно сказать";
                case 34 -> response = "Хм...";
                case 35 -> response = "Загадочно";
                case 36 -> response = "Не уверен в этом";
                case 37 -> response = "Прекрасно";
                case 38 -> response = "Неожиданно";
                case 39 -> response = "Любопытно";
                case 40 -> response = "Довольно странно";
                case 41 -> response = "Согласен!";
                case 42 -> response = "Интересное мнение";
                case 43 -> response = "Точно!";
                case 44 -> response = "Не думаю";
                case 45 -> response = "Это не так";
                case 46 -> response = "Возможно";
                case 47 -> response = "Не уверен";
                case 48 -> response = "Да!";
                case 49 -> response = "Нет";
                case 50 -> response = "Может быть";
                case 51 -> response = "Скорее да";
                case 52 -> response = "Скорее нет";
                case 53 -> response = "Абсолютно!";
                case 54 -> response = "Никак нет";
                case 55 -> response = "Наверное";
                case 56 -> response = "Трудно сказать";
                case 57 -> response = "Это точно";
                case 58 -> response = "Не думаю";
                case 59 -> response = "Да уж";
                case 60 -> response = "Не знаю";
                case 61 -> response = "Это надо обсудить";
                case 62 -> response = "Вполне возможно";
                case 63 -> response = "Никогда не слышал";
                case 64 -> response = "Прекрасно";
                case 65 -> response = "Это интересно";
                case 66 -> response = "Неужели?";
                case 67 -> response = "Звучит неплохо";
                case 68 -> response = "Как сказать";
                case 69 -> response = "Удивительно!";
                case 70 -> response = "Это что-то новенькое";
                case 71 -> response = "Я не уверен";
                case 72 -> response = "Пожалуй";
                case 73 -> response = "Не совсем";
                case 74 -> response = "Это необходимо";
                case 75 -> response = "Возможно";
                case 76 -> response = "Думаю, да";
                case 77 -> response = "Думаю, нет";
                case 78 -> response = "Это важно";
                case 79 -> response = "Не уверен";
                case 80 -> response = "Может быть";
                case 81 -> response = "Это возможно";
                case 82 -> response = "Как знать";
                case 83 -> response = "Не думаю";
                case 84 -> response = "Это точно";
                case 85 -> response = "Не совсем";
                case 86 -> response = "Как бы не так";
                case 87 -> response = "Не знаю";
                case 88 -> response = "Трудно сказать";
                case 89 -> response = "Да!";
                case 90 -> response = "Нет";
                case 91 -> response = "Может быть";
                case 92 -> response = "Наверное";
                case 93 -> response = "Навряд ли";
                case 94 -> response = "Это точно";
                case 95 -> response = "Не уверен";
                case 96 -> response = "Да уж";
                case 97 -> response = "Это так";
                case 98 -> response = "Скорее да";
                case 99 -> response = "Скорее нет";
                default -> response = "Как ты сюда попал? Ты не должен видеть это сообщение.";
            }

            sendTextMessage(chatId, response);

            switch (messageText) {
                case "/start" -> {
                    subscriptionService.subscribe(chatId);
                    sendTextMessage(chatId, "Привет!");
                }
                case "/stop" -> {
                    subscriptionService.unsubscribe(chatId);
                    sendTextMessage(chatId, "Пока!");
                }
            }
        }
    }

    private void sendTextMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}