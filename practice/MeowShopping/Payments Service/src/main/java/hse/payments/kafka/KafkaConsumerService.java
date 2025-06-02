package hse.payments.kafka;

import hse.payments.kafka.events.InboxEvent;
import hse.payments.kafka.events.OrderAddedEvent;
import hse.payments.kafka.inbox.InboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final InboxEventRepository inboxRepository;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-payment", groupId = "hse-shopping")
    @Transactional
    public void handleAddedOrder(String event) {

        // Проблема: если БД order сервиса умирала (и очистилась), то новые созданные там заказы не будут обработаны здесь

        OrderAddedEvent orderAddedEvent = null;

        try {
            orderAddedEvent = objectMapper.readValue(event, OrderAddedEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации заказа", e);
        }

        if (inboxRepository.existsById(Long.valueOf(orderAddedEvent.orderId()))) {
            return;
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