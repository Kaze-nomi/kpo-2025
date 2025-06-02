package hse.orders.kafka.outbox;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import hse.orders.domains.Order;
import hse.orders.kafka.KafkaProducerService;
import hse.orders.kafka.events.OutboxEvent;
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

            Order order = null;

            try {
                order = objectMapper.readValue(event.getPayload(), Order.class);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка десериализации заказа", e);
            }
            kafkaTemplate.processOrder(order);
            event.setSent(true);
            repository.save(event);
        }
        
    }
}