package hse.orders.kafka;

import hse.orders.domains.Order;
import hse.orders.domains.Progress;
import hse.orders.kafka.events.OrderProcessedEvent;
import hse.orders.repositories.OrdersRepository;
import hse.orders.websocket.OrderNotificationController;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final OrdersRepository ordersRepository;

    private final ObjectMapper objectMapper;

    private final OrderNotificationController notificationController;

    @KafkaListener(topics = "order-recieve-payment-status", groupId = "hse-shopping")
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

        notificationController.notifyOrderUpdate(order);
        
    }
}