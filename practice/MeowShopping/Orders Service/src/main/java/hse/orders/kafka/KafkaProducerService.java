package hse.orders.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import hse.orders.domains.Order;
import hse.orders.kafka.events.OrderAddedEvent;
import hse.orders.websocket.OrderNotificationController;

@Service
public class KafkaProducerService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OrderNotificationController notificationController;


    @Autowired
    private ObjectMapper objectMapper;

    public void processOrder(Order order) {
    
        OrderAddedEvent event = new OrderAddedEvent(
            order.getId(),
            order.getUserId(),
            order.getAmount(),
            order.getDescription()
        );

        String payload = null;

        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации заказа", e);
        }

        kafkaTemplate.send("order-payment", payload);

        notificationController.notifyOrderUpdate(order);
        
    }
}