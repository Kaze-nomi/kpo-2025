package hse.orders.kafka;

import hse.orders.domains.Order;
import hse.orders.domains.Progress;
import hse.orders.kafka.events.OrderProcessedEvent;
import hse.orders.repositories.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final OrdersRepository ordersRepository;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-recieve-payment-status", groupId = "hse-shopping")
    @Transactional
    public void handleOrderProcessingUpdate(String event) {

        OrderProcessedEvent orderProcessed = null;

        try {
            orderProcessed = objectMapper.readValue(event, OrderProcessedEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации заказа", e);
        }

        Order order = ordersRepository.findById(orderProcessed.orderId()).orElse(null);

        if (order == null) {
            return;
        }

        if (order.getStatus() != Progress.NEW) {
            return;
        }
        
        if (orderProcessed.isSuccess()) {
            order.setStatus(Progress.FINISHED);}
        else {
            order.setStatus(Progress.CANCELLED);
        }

        ordersRepository.save(order);
        
    }
}