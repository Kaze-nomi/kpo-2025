package hse.payments.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import hse.payments.kafka.events.OrderProcessedEvent;

@Service
public class KafkaProducerService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void returnToSender(OrderProcessedEvent event) {

        String eventPayload = null;

        try {
            eventPayload = objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации заказа", e);
        }

        kafkaTemplate.send("order-recieve-payment-status", eventPayload);

    }
}