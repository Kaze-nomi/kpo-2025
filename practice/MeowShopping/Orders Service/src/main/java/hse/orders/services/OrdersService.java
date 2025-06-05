package hse.orders.services;

import hse.orders.domains.Order;
import hse.orders.kafka.events.OutboxEvent;
import hse.orders.kafka.outbox.OutboxEventRepository;
import hse.orders.repositories.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final OrdersRepository ordersRepository;

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<Order> getOrders(Integer userId) {
        return ordersRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Order getOrderStatus(Integer orderId) {
        return ordersRepository.findById(orderId).orElse(null);
    }

    @Transactional
    public Order createOrder(Integer userId, double amount, String description) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        order.setDescription(description);
        var savedOrder = ordersRepository.save(order);
        saveToOutbox(savedOrder);
        return savedOrder;
    }

    private void saveToOutbox(Order order) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("Создание заказа");
        outboxEvent.setCreatedAt(java.time.LocalDateTime.now());

        try {
            outboxEvent.setPayload(objectMapper.writeValueAsString(order));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации заказа", e);
        }

        outboxEventRepository.save(outboxEvent);
    }

}