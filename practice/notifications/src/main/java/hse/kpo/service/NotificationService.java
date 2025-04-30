package hse.kpo.service;

import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import hse.kpo.entities.CustomerData;
import hse.kpo.grpc.ReportResponse;
import hse.kpo.grpc.ReportServiceGrpc;
import hse.kpo.telegram.NotificationBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ReportServiceGrpc.ReportServiceBlockingStub reportService;
    private final NotificationBot notificationBot;
    private final SubscriptionService subscriptionService;

    @Scheduled(fixedRate = 60_000)
    public void checkSalesAndNotify() {
        log.warn("getting report");
        ReportResponse report = reportService.getLatestReport(null);
        parseAndSendNotifications(report.getContent());
    }

    private void parseAndSendNotifications(String reportContent) {
        List<String> lines = Arrays.stream(reportContent.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        // Парсинг продаж
        Map<String, Long> salesByType = lines.stream()
                .filter(line -> line.startsWith("Операция: Продажа:"))
                .map(this::parseSaleType)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(type -> type, LinkedHashMap::new, Collectors.counting()));

        // Парсинг клиентов
        List<CustomerData> customers = lines.stream()
                .filter(line -> line.startsWith("Покупатели:"))
                .map(this::parseCustomer)
                .filter(c -> c != CustomerData.INVALID)
                .collect(Collectors.toList());

        if (salesByType.isEmpty() && customers.isEmpty())
            return;

        StringBuilder message = new StringBuilder("🎉 *Свежий отчёт!* 🎉\n\n");

        if (!salesByType.isEmpty()) {
            message.append("🚗 *Топ продаж:*\n");
            salesByType.forEach((type, count) -> message
                    .append(String.format("- %s: %d шт. %s\n", type, count, getCarEmoji(type))));
            message.append("\n");
        }

        if (!customers.isEmpty()) {
            message.append("👥 *Лучшие клиенты:*\n");

            customers.stream()
                    .max(Comparator.comparingInt(CustomerData::getHandPower))
                    .ifPresent(c -> message.append(String.format(
                            "💪 Силач: %s (руки: %d) %s\n",
                            c.getName(),
                            c.getHandPower(),
                            c.getHandPower() > 80 ? "🔥" : "")));

            customers.stream()
                    .max(Comparator.comparingInt(CustomerData::getLegPower))
                    .ifPresent(c -> message.append(String.format(
                            "🦵 Спринтер: %s (ноги: %d) %s\n",
                            c.getName(),
                            c.getLegPower(),
                            c.getLegPower() > 100 ? "⚡" : "")));

            customers.stream()
                    .max(Comparator.comparingInt(CustomerData::getIq))
                    .ifPresent(c -> message.append(String.format(
                            "🧠 Гений: %s (IQ: %d) %s\n",
                            c.getName(),
                            c.getIq(),
                            c.getIq() > 130 ? "🌟" : "")));

            message.append("\n📌 *Интересное:*\n")
                    .append(String.format("🚘 Всего машин: %d\n",
                            customers.stream().mapToInt(CustomerData::getCarsCount).sum()))
                    .append(String.format("⛵ Корабли: %d чел.\n",
                            customers.stream().filter(c -> c.getCatamaransCount() > 0).count()))
                    .append(getRandomFact(customers));
        }

        sendToTelegram(message.toString());
    }

    private String getCarEmoji(String type) {
        return switch (type.toUpperCase()) {
            case "SUV" -> "🚙";
            case "SEDAN" -> "🚗";
            case "HATCHBACK" -> "🚐";
            default -> "🏎️";
        };
    }

    private String getRandomFact(List<CustomerData> customers) {
        List<String> facts = new ArrayList<>();

        if (customers.stream().anyMatch(c -> c.getIq() > 140)) {
            facts.add("🔝 У нас есть клиент с IQ гения!");
        }

        if (customers.stream().anyMatch(c -> c.getCarsCount() > 5)) {
            facts.add("🏁 Наш клиент - настоящий коллекционер авто!");
        }

        return !facts.isEmpty()
                ? "\n💡 Факт дня: " + facts.get(new Random().nextInt(facts.size()))
                : "\n✨ Сегодня все клиенты особенные!";
    }

    private CustomerData parseCustomer(String raw) {
        try {
            String name = extractValue(raw, "name=([^,]+)");
            int legPower = parseInt(extractValue(raw, "legPower=(\\d+)"));
            int handPower = parseInt(extractValue(raw, "handPower=(\\d+)"));
            int iq = parseInt(extractValue(raw, "iq=(\\d+)"));

            // Парсинг количества машин
            String carsStr = extractValue(raw, "cars=\\[(.*?)]");
            int carsCount = 0;
            if (carsStr != null && !carsStr.isEmpty()) {
                carsCount = (int) Pattern.compile("Car\\(").matcher(carsStr).results().count();
            }

            // Парсинг катамаранов
            String shipValue = extractValue(raw, "ship=([^,)]+)");
            int catamaransCount = "null".equals(shipValue) ? 0 : 1;

            return CustomerData.createValid(
                    name.trim(),
                    legPower,
                    handPower,
                    iq,
                    carsCount,
                    catamaransCount);
        } catch (Exception e) {
            log.error("Error parsing customer: {}", raw, e);
            return CustomerData.INVALID;
        }
    }

    private String parseSaleType(String saleLine) {
        Matcher matcher = Pattern.compile("Продажа: (\\w+) VIN-\\d+").matcher(saleLine);
        return matcher.find() ? matcher.group(1) : null;
    }

    private void sendToTelegram(String message) {
        log.warn("sending to tg");
        Set<String> activeChats = subscriptionService.getActiveSubscriptions();

        activeChats.forEach(chatId -> {
            SendMessage sendMessage = new SendMessage(chatId, message);
            try {
                notificationBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Failed to send message to chat {}: {}", chatId, e.getMessage());
            }
        });
    }

    private String extractValue(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(1).trim() : null;
    }
}