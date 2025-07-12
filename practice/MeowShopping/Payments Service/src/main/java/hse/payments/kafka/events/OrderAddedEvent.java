package hse.payments.kafka.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderAddedEvent(
                int orderId,
                int userId,
                double amount,
                String description) {
}