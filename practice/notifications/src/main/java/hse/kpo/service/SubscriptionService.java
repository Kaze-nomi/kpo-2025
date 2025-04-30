package hse.kpo.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class SubscriptionService {
    private static final Path FILE_PATH = Paths.get("subscriptions.txt");

    public SubscriptionService() {
        try {
            if (!Files.exists(FILE_PATH)) {
                Files.createFile(FILE_PATH);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize subscriptions file", e);
        }
    }

    public void subscribe(String chatId) {
        try {
            Set<String> activeChatIds = getActiveSubscriptions();
            activeChatIds.add(chatId);
            Files.write(FILE_PATH, activeChatIds);
        } catch (IOException e) {
            throw new RuntimeException("Failed to subscribe", e);
        }
    }

    public void unsubscribe(String chatId) {
        try {
            Set<String> activeChatIds = getActiveSubscriptions();
            activeChatIds.remove(chatId);
            Files.write(FILE_PATH, activeChatIds);
        } catch (IOException e) {
            throw new RuntimeException("Failed to unsubscribe", e);
        }
    }

    public Set<String> getActiveSubscriptions() {
        try {
            return new HashSet<>(Files.readAllLines(FILE_PATH));
        } catch (IOException e) {
            return Collections.emptySet();
        }
    }
}

