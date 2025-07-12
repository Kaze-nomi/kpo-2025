package hse.payments.KafkaTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hse.payments.kafka.KafkaConsumerService;
import hse.payments.kafka.KafkaProducerService;
import hse.payments.kafka.events.InboxEvent;
import hse.payments.kafka.events.OrderAddedEvent;
import hse.payments.kafka.events.OrderProcessedEvent;
import hse.payments.kafka.inbox.InboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
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
    private InboxEventRepository inboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Captor
    private ArgumentCaptor<InboxEvent> inboxEventCaptor;

    @Captor
    private ArgumentCaptor<String> kafkaPayloadCaptor;

    private final int testOrderId = 456;
    private final int testUserId = 123;
    private final double testAmount = 50.0;
    private final String testDescription = "Test order";

    // ========== KafkaConsumerService Tests ==========

    @Test
    void handleAddedOrder_NewEvent_SavesToRepository() throws Exception {
        // Arrange
        String payload = createOrderAddedPayload();
        OrderAddedEvent orderEvent = new OrderAddedEvent(testOrderId, testUserId, testAmount, testDescription);
        
        when(objectMapper.readValue(payload, OrderAddedEvent.class))
            .thenReturn(orderEvent);
        when(inboxRepository.findById((long) testOrderId))
            .thenReturn(Optional.empty());

        when(objectMapper.writeValueAsString(any(OrderAddedEvent.class)))
            .thenReturn("{\"orderId\":" + testOrderId + ",\"userId\":" + testUserId + ",\"amount\":" + testAmount + ",\"description\":\"" + testDescription + "\"}");

        // Act
        kafkaConsumerService.handleAddedOrder(payload);

        // Assert
        verify(inboxRepository).save(inboxEventCaptor.capture());
        
        InboxEvent savedEvent = inboxEventCaptor.getValue();
        assertEquals(testOrderId, savedEvent.getId());
        assertEquals("Добавлен заказ на оплату", savedEvent.getEventType());
        assertEquals(payload, savedEvent.getPayload());
        assertFalse(savedEvent.isProcessed());
        assertNotNull(savedEvent.getCreatedAt());
    }

    @Test
    void handleAddedOrder_DuplicateEvent_Ignores() throws Exception {
        // Arrange
        String payload = createOrderAddedPayload();
        OrderAddedEvent orderEvent = new OrderAddedEvent(testOrderId, testUserId, testAmount, testDescription);
        InboxEvent existingEvent = createInboxEvent();
        
        when(objectMapper.readValue(payload, OrderAddedEvent.class))
            .thenReturn(orderEvent);
        when(inboxRepository.findById((long) testOrderId))
            .thenReturn(Optional.of(existingEvent));

        // Act
        kafkaConsumerService.handleAddedOrder(payload);

        // Assert
        verify(inboxRepository, never()).save(any());
    }

    @Test
    void handleAddedOrder_DeserializationFailure_ThrowsException() throws Exception {
        // Arrange
        String invalidPayload = "invalid_json";
        when(objectMapper.readValue(invalidPayload, OrderAddedEvent.class))
            .thenThrow(new JsonProcessingException("Error") {});

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            kafkaConsumerService.handleAddedOrder(invalidPayload)
        );
    }

    // ========== KafkaProducerService Tests ==========

    @Test
    void returnToSender_ValidEvent_SendsToKafka() throws Exception {
        // Arrange
        OrderProcessedEvent event = new OrderProcessedEvent(testOrderId, true);
        String expectedPayload = "{\"orderId\":" + testOrderId + ",\"isSuccess\":true}";
        
        when(objectMapper.writeValueAsString(event))
            .thenReturn(expectedPayload);

        // Act
        kafkaProducerService.returnToSender(event);

        // Assert
        verify(kafkaTemplate).send(eq("order-recieve-payment-status"), kafkaPayloadCaptor.capture());
        assertEquals(expectedPayload, kafkaPayloadCaptor.getValue());
    }

    @Test
    void returnToSender_SerializationFailure_ThrowsException() throws Exception {
        // Arrange
        OrderProcessedEvent event = new OrderProcessedEvent(testOrderId, true);
        when(objectMapper.writeValueAsString(event))
            .thenThrow(new JsonProcessingException("Error") {});

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            kafkaProducerService.returnToSender(event)
        );
    }

    // ========== Helper Methods ==========

    private String createOrderAddedPayload() {
        return String.format(
            "{\"orderId\":%d,\"userId\":%d,\"amount\":%.1f,\"description\":\"%s\"}",
            testOrderId, testUserId, testAmount, testDescription
        );
    }

    private InboxEvent createInboxEvent() {
        InboxEvent event = new InboxEvent();
        event.setId((long) testOrderId);
        event.setPayload(createOrderAddedPayload());
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }
    
}