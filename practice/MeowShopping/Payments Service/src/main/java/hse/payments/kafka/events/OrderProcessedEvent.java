package hse.payments.kafka.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderProcessedEvent(
        int orderId,
        boolean isSuccess) {
}
