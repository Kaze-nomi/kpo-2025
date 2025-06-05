package hse.payments.kafka;

import hse.payments.kafka.events.InboxEvent;
import hse.payments.kafka.events.OrderAddedEvent;
import hse.payments.kafka.inbox.InboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor

public class KafkaConsumerService {

    private final InboxEventRepository inboxRepository;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-payment", groupId = "hse-shopping")
    @Transactional
    @Retryable(
            value = { 
                    ObjectOptimisticLockingFailureException.class,
                    DataAccessResourceFailureException.class,
                    TransientDataAccessException.class 
            }, 
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2.0)
    )
    public void handleAddedOrder(String event) {

        OrderAddedEvent orderAddedEvent = null;

        try {
            orderAddedEvent = objectMapper.readValue(event, OrderAddedEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации заказа", e);
        }

        if (inboxRepository.findById(Long.valueOf(orderAddedEvent.orderId())).isPresent()) {
            if (inboxRepository.findById(Long.valueOf(orderAddedEvent.orderId())).get().getPayload().equals(event)) {
                return;
            }
        }

        InboxEvent inboxEvent = new InboxEvent();
        inboxEvent.setId(Long.valueOf(orderAddedEvent.orderId()));
        inboxEvent.setEventType("Добавлен заказ на оплату");
        try {
            inboxEvent.setPayload(objectMapper.writeValueAsString(orderAddedEvent));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации заказа", e);
        }
        inboxEvent.setCreatedAt(LocalDateTime.now());

        inboxRepository.save(inboxEvent);

    }

}