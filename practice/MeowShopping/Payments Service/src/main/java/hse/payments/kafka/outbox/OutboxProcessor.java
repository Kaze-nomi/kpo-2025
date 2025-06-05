package hse.payments.kafka.outbox;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import hse.payments.kafka.KafkaProducerService;
import hse.payments.kafka.events.OrderProcessedEvent;
import hse.payments.kafka.events.OutboxEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxEventRepository repository;

    private final KafkaProducerService kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processOutboxEvents() {

        List<OutboxEvent> events = repository.findAllBySentFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {
            OrderProcessedEvent orderProcessed = null;
            try {
                orderProcessed = objectMapper.readValue(event.getPayload(), OrderProcessedEvent.class);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка десериализации заказа", e);
            }
            kafkaTemplate.returnToSender(orderProcessed);
            event.setSent(true);
            repository.save(event);
        }

    }
}