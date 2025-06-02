package hse.payments.kafka.inbox;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import hse.payments.kafka.events.InboxEvent;
import hse.payments.kafka.events.OrderAddedEvent;
import hse.payments.kafka.events.OrderProcessedEvent;
import hse.payments.kafka.events.OutboxEvent;
import hse.payments.kafka.outbox.OutboxEventRepository;
import hse.payments.services.PaymentsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

// Что здесь и в KafkaConsumerService что будет если два потока одновременно начнут выполнение? Может ли быть такое что запишется сразу два 
// одинаковых события в outbox? 

@Component
@RequiredArgsConstructor
public class InboxProcessor {

    private final PaymentsService paymentsService;

    private final InboxEventRepository inboxRepository;

    private final OutboxEventRepository outboxRepository;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processInboxEvents() {

        List<InboxEvent> events = inboxRepository.findAllByProcessedFalseOrderByCreatedAtAsc();

        for (InboxEvent event : events) {

            try {

                OrderAddedEvent orderAddedEvent = objectMapper.readValue(event.getPayload(), OrderAddedEvent.class);

                boolean success = paymentsService.withdraw(orderAddedEvent.userId(), orderAddedEvent.amount());

                OrderProcessedEvent orderProcessed = new OrderProcessedEvent(
                        orderAddedEvent.orderId(),
                        success);

                String orderProcessedPayload = objectMapper.writeValueAsString(orderProcessed);

                OutboxEvent outboxEvent = new OutboxEvent();
                outboxEvent.setId(Long.valueOf(orderAddedEvent.orderId()));
                outboxEvent.setEventType("Заказ обработан");
                outboxEvent.setPayload(orderProcessedPayload);
                outboxEvent.setSent(false);
                outboxEvent.setCreatedAt(LocalDateTime.now());

                outboxRepository.save(outboxEvent);

                event.setProcessed(true);

                inboxRepository.save(event);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}