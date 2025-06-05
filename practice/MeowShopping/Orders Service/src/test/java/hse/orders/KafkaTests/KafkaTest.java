package hse.orders.KafkaTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.orders.domains.Order;
import hse.orders.domains.Progress;
import hse.orders.kafka.KafkaConsumerService;
import hse.orders.kafka.KafkaProducerService;
import hse.orders.kafka.events.OrderAddedEvent;
import hse.orders.kafka.events.OrderProcessedEvent;
import hse.orders.repositories.OrdersRepository;
import hse.orders.websocket.OrderNotificationController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderNotificationController notificationController;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Captor
    private ArgumentCaptor<String> kafkaPayloadCaptor;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private final int testOrderId = 1;
    private final int testUserId = 100;
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

    // ========== KafkaProducerService Tests ==========

    @Test
    void processOrder_ValidOrder_SendsToKafkaAndNotifies() throws Exception {
        // Arrange
        Order order = createTestOrder();
        OrderAddedEvent expectedEvent = new OrderAddedEvent(
                testOrderId, testUserId, testAmount, testDescription
        );
        
        when(objectMapper.writeValueAsString(expectedEvent))
                .thenReturn("serialized_event");

        // Act
        kafkaProducerService.processOrder(order);

        // Assert
        verify(kafkaTemplate).send(eq("order-payment"), kafkaPayloadCaptor.capture());
        verify(notificationController).notifyOrderUpdate(orderCaptor.capture());
        
        assertEquals("serialized_event", kafkaPayloadCaptor.getValue());
        assertEquals(order, orderCaptor.getValue());
    }

    @Test
    void processOrder_SerializationFailure_ThrowsException() throws Exception {
        // Arrange
        Order order = createTestOrder();
        when(objectMapper.writeValueAsString(any(OrderAddedEvent.class)))
                .thenThrow(new RuntimeException("Serialization error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            kafkaProducerService.processOrder(order)
        );
    }

    // ========== KafkaConsumerService Tests ==========

    @Test
    void handleOrderProcessingUpdate_SuccessPayment_UpdatesToFinished() throws Exception {
        // Arrange
        String eventJson = "{\"orderId\":1,\"isSuccess\":true}";
        Order order = createTestOrder();
        
        when(objectMapper.readValue(eq(eventJson), eq(OrderProcessedEvent.class)))
                .thenReturn(new OrderProcessedEvent(testOrderId, true));
        when(ordersRepository.findById(testOrderId))
                .thenReturn(Optional.of(order));

        // Act
        kafkaConsumerService.handleOrderProcessingUpdate(eventJson);

        // Assert
        assertEquals(Progress.FINISHED, order.getStatus());
        verify(ordersRepository).save(order);
        verify(notificationController).notifyOrderUpdate(order);
    }

    @Test
    void handleOrderProcessingUpdate_FailedPayment_UpdatesToCancelled() throws Exception {
        // Arrange
        String eventJson = "{\"orderId\":1,\"isSuccess\":false}";
        Order order = createTestOrder();
        
        when(objectMapper.readValue(eq(eventJson), eq(OrderProcessedEvent.class)))
                .thenReturn(new OrderProcessedEvent(testOrderId, false));
        when(ordersRepository.findById(testOrderId))
                .thenReturn(Optional.of(order));

        // Act
        kafkaConsumerService.handleOrderProcessingUpdate(eventJson);

        // Assert
        assertEquals(Progress.CANCELLED, order.getStatus());
        verify(ordersRepository).save(order);
        verify(notificationController).notifyOrderUpdate(order);
    }

    @Test
    void handleOrderProcessingUpdate_OrderNotFound_DoesNothing() throws Exception {
        // Arrange
        String eventJson = "{\"orderId\":999,\"isSuccess\":true}";
        
        when(objectMapper.readValue(eq(eventJson), eq(OrderProcessedEvent.class)))
                .thenReturn(new OrderProcessedEvent(999, true));
        when(ordersRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act
        kafkaConsumerService.handleOrderProcessingUpdate(eventJson);

        // Assert
        verify(ordersRepository, never()).save(any());
        verify(notificationController, never()).notifyOrderUpdate(any());
    }

    @Test
    void handleOrderProcessingUpdate_OrderNotNew_DoesNothing() throws Exception {
        // Arrange
        String eventJson = "{\"orderId\":1,\"isSuccess\":true}";
        Order order = createTestOrder();
        order.setStatus(Progress.FINISHED);
        
        when(objectMapper.readValue(eq(eventJson), eq(OrderProcessedEvent.class)))
                .thenReturn(new OrderProcessedEvent(testOrderId, true));
        when(ordersRepository.findById(testOrderId))
                .thenReturn(Optional.of(order));

        // Act
        kafkaConsumerService.handleOrderProcessingUpdate(eventJson);

        // Assert
        verify(ordersRepository, never()).save(any());
        verify(notificationController, never()).notifyOrderUpdate(any());
    }

    @Test
    void handleOrderProcessingUpdate_DeserializationFailure_ThrowsException() throws Exception {
        // Arrange
        String eventJson = "invalid_json";
        when(objectMapper.readValue(eq(eventJson), eq(OrderProcessedEvent.class)))
                .thenThrow(new RuntimeException("Deserialization error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            kafkaConsumerService.handleOrderProcessingUpdate(eventJson)
        );
    }
    
}