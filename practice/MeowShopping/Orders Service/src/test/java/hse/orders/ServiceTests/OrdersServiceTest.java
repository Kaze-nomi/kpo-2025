package hse.orders.ServiceTests;

import hse.orders.domains.Order;
import hse.orders.domains.Progress;
import hse.orders.kafka.events.OutboxEvent;
import hse.orders.kafka.outbox.OutboxEventRepository;
import hse.orders.repositories.OrdersRepository;
import hse.orders.services.OrdersService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionSystemException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrdersService ordersService;

    @Captor
    private ArgumentCaptor<OutboxEvent> outboxEventCaptor;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private final int testUserId = 1;
    private final int testOrderId = 100;
    private final double testAmount = 150.0;
    private final String testDescription = "Test order";

    private Order createTestOrder() {
        Order order = new Order();
        order.setId(testOrderId);
        order.setUserId(testUserId);
        order.setAmount(testAmount);
        order.setDescription(testDescription);
        order.setStatus(Progress.NEW);
        return order;
    }

    // ------------------------- getOrders Tests -------------------------

    @Test
    void getOrders_ValidUserId_ReturnsOrders() {
        // Arrange
        Order order1 = createTestOrder();
        Order order2 = createTestOrder();
        order2.setId(101);
        
        when(ordersRepository.findAllByUserId(testUserId))
            .thenReturn(Arrays.asList(order1, order2));
        
        // Act
        List<Order> result = ordersService.getOrders(testUserId);
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(testOrderId, result.get(0).getId());
        assertEquals(101, result.get(1).getId());
    }

    @Test
    void getOrders_NoOrders_ReturnsEmptyList() {
        // Arrange
        when(ordersRepository.findAllByUserId(testUserId))
            .thenReturn(Collections.emptyList());
        
        // Act
        List<Order> result = ordersService.getOrders(testUserId);
        
        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getOrders_DatabaseError_ThrowsException() {
        // Arrange
        when(ordersRepository.findAllByUserId(anyInt()))
            .thenThrow(new TransactionSystemException("DB error"));
        
        // Act & Assert
        assertThrows(TransactionSystemException.class, () -> 
            ordersService.getOrders(testUserId)
        );
    }

    // ------------------------- getOrderStatus Tests -------------------------

    @Test
    void getOrderStatus_ValidOrderId_ReturnsOrder() {
        // Arrange
        Order order = createTestOrder();
        when(ordersRepository.findById(testOrderId))
            .thenReturn(Optional.of(order));
        
        // Act
        Order result = ordersService.getOrderStatus(testOrderId);
        
        // Assert
        assertNotNull(result);
        assertEquals(testOrderId, result.getId());
    }

    @Test
    void getOrderStatus_OrderNotFound_ReturnsNull() {
        // Arrange
        when(ordersRepository.findById(testOrderId))
            .thenReturn(Optional.empty());
        
        // Act
        Order result = ordersService.getOrderStatus(testOrderId);
        
        // Assert
        assertNull(result);
    }

    // ------------------------- createOrder Tests -------------------------

    @Test
    void createOrder_ValidData_CreatesOrderAndOutboxEvent() throws JsonProcessingException {
        // Arrange
        Order savedOrder = createTestOrder();
        when(ordersRepository.save(any(Order.class)))
            .thenReturn(savedOrder);
        when(objectMapper.writeValueAsString(any(Order.class)))
            .thenReturn("serialized_order");
        
        // Act
        Order result = ordersService.createOrder(testUserId, testAmount, testDescription);
        
        // Assert
        assertNotNull(result);
        assertEquals(testOrderId, result.getId());
        
        // Verify order save
        verify(ordersRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertEquals(testUserId, capturedOrder.getUserId());
        assertEquals(testAmount, capturedOrder.getAmount());
        assertEquals(testDescription, capturedOrder.getDescription());
        assertEquals(Progress.NEW, capturedOrder.getStatus());
        
        // Verify outbox event creation
        verify(outboxEventRepository).save(outboxEventCaptor.capture());
        OutboxEvent outboxEvent = outboxEventCaptor.getValue();
        assertEquals("Создание заказа", outboxEvent.getEventType());
        assertEquals("serialized_order", outboxEvent.getPayload());
        assertFalse(outboxEvent.isSent());
        assertNotNull(outboxEvent.getCreatedAt());
    }

    @Test
    void createOrder_SerializationFailure_ThrowsException() throws JsonProcessingException {
        // Arrange
        Order savedOrder = createTestOrder();
        when(ordersRepository.save(any(Order.class)))
            .thenReturn(savedOrder);
        when(objectMapper.writeValueAsString(any(Order.class)))
            .thenThrow(new JsonProcessingException("Serialization error") {});
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            ordersService.createOrder(testUserId, testAmount, testDescription)
        );
        
        assertTrue(exception.getMessage().contains("Ошибка сериализации заказа"));
    }

    @Test
    void createOrder_DatabaseSaveFailure_ThrowsException() {
        // Arrange
        when(ordersRepository.save(any(Order.class)))
            .thenThrow(new DataAccessException("DB save failed") {});
        
        // Act & Assert
        assertThrows(DataAccessException.class, () -> 
            ordersService.createOrder(testUserId, testAmount, testDescription)
        );
    }
    
}