package hse.orders.KafkaTests;

import hse.orders.domains.Order;
import hse.orders.domains.Progress;
import hse.orders.kafka.KafkaProducerService;
import hse.orders.kafka.events.OutboxEvent;
import hse.orders.kafka.outbox.OutboxEventRepository;
import hse.orders.kafka.outbox.OutboxProcessor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxProcessorTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxProcessor outboxProcessor;

    @Captor
    private ArgumentCaptor<OutboxEvent> outboxEventCaptor;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private final int testOrderId = 1;
    private final int testUserId = 100;
    private final double testAmount = 150.0;
    private final String testDescription = "Test order";

    private OutboxEvent createOutboxEvent(String payload, boolean sent) {
        OutboxEvent event = new OutboxEvent();
        event.setId(1L);
        event.setEventType("Создание заказа");
        event.setPayload(payload);
        event.setSent(sent);
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }

    private Order createTestOrder() {
        Order order = new Order();
        order.setId(testOrderId);
        order.setUserId(testUserId);
        order.setAmount(testAmount);
        order.setDescription(testDescription);
        order.setStatus(Progress.NEW);
        return order;
    }

    @Test
    void processOutboxEvents_WithUnsentEvents_ProcessesAll() throws Exception {
        // Arrange
        OutboxEvent event1 = createOutboxEvent("payload1", false);
        OutboxEvent event2 = createOutboxEvent("payload2", false);
        
        when(outboxEventRepository.findAllBySentFalseOrderByCreatedAtAsc())
            .thenReturn(Arrays.asList(event1, event2));
        
        Order order1 = createTestOrder();
        Order order2 = createTestOrder();
        order2.setId(2);
        
        when(objectMapper.readValue("payload1", Order.class))
            .thenReturn(order1);
        when(objectMapper.readValue("payload2", Order.class))
            .thenReturn(order2);

        // Act
        outboxProcessor.processOutboxEvents();

        // Assert
        // Verify events were processed
        verify(kafkaProducerService, times(2)).processOrder(orderCaptor.capture());
        List<Order> processedOrders = orderCaptor.getAllValues();
        assertEquals(2, processedOrders.size());
        assertEquals(testOrderId, processedOrders.get(0).getId());
        assertEquals(2, processedOrders.get(1).getId());
        
        // Verify events were marked as sent
        verify(outboxEventRepository, times(2)).save(outboxEventCaptor.capture());
        List<OutboxEvent> savedEvents = outboxEventCaptor.getAllValues();
        assertTrue(savedEvents.get(0).isSent());
        assertTrue(savedEvents.get(1).isSent());
    }

    @Test
    void processOutboxEvents_NoUnsentEvents_DoesNothing() {
        // Arrange
        when(outboxEventRepository.findAllBySentFalseOrderByCreatedAtAsc())
            .thenReturn(Collections.emptyList());

        // Act
        outboxProcessor.processOutboxEvents();

        // Assert
        verifyNoInteractions(kafkaProducerService);
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    void processOutboxEvents_DeserializationFailure_ThrowsException() throws Exception {
        // Arrange
        OutboxEvent event = createOutboxEvent("invalid_payload", false);
        when(outboxEventRepository.findAllBySentFalseOrderByCreatedAtAsc())
            .thenReturn(Collections.singletonList(event));
        
        when(objectMapper.readValue("invalid_payload", Order.class))
            .thenThrow(new RuntimeException("Deserialization error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> outboxProcessor.processOutboxEvents()
        );
        
        assertTrue(exception.getMessage().contains("Ошибка десериализации заказа"));
        
        // Verify event was not marked as sent
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    void processOutboxEvents_MixedSentStatus_ProcessesOnlyUnsent() {
        // Arrange
        OutboxEvent unsentEvent = createOutboxEvent("payload1", false);
        OutboxEvent sentEvent = createOutboxEvent("payload2", true); // Already sent
        
        when(outboxEventRepository.findAllBySentFalseOrderByCreatedAtAsc())
            .thenReturn(Arrays.asList(unsentEvent, sentEvent).stream()
                .filter(event -> !event.isSent())
                .toList());

        // Act
        outboxProcessor.processOutboxEvents();

        // Assert
        verify(kafkaProducerService, times(1)).processOrder(any());
        verify(outboxEventRepository, times(1)).save(any());
    }

    @Test
    void processOutboxEvents_ProcessingFailure_WasNotMarkedAsSent() throws Exception {
        // Arrange
        OutboxEvent event = createOutboxEvent("payload", false);
        when(outboxEventRepository.findAllBySentFalseOrderByCreatedAtAsc())
            .thenReturn(Collections.singletonList(event));
        
        Order order = createTestOrder();
        when(objectMapper.readValue("payload", Order.class))
            .thenReturn(order);
        
        doThrow(new RuntimeException("Kafka error"))
            .when(kafkaProducerService).processOrder(any());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> outboxProcessor.processOutboxEvents()
        );
        
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    void processOutboxEvents_OrderedByCreatedAtAsc() {
        // Arrange
        OutboxEvent event1 = createOutboxEvent("payload1", false);
        event1.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        
        OutboxEvent event2 = createOutboxEvent("payload2", false);
        event2.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        
        OutboxEvent event3 = createOutboxEvent("payload3", false);
        event3.setCreatedAt(LocalDateTime.now());
        
        when(outboxEventRepository.findAllBySentFalseOrderByCreatedAtAsc())
            .thenReturn(Arrays.asList(event1, event2, event3));

        // Act
        outboxProcessor.processOutboxEvents();

        // Assert
        verify(kafkaProducerService, times(3)).processOrder(any());
        verify(outboxEventRepository, times(3)).save(outboxEventCaptor.capture());
        
        List<OutboxEvent> savedEvents = outboxEventCaptor.getAllValues();
        assertEquals(event1.getId(), savedEvents.get(0).getId());
        assertEquals(event2.getId(), savedEvents.get(1).getId());
        assertEquals(event3.getId(), savedEvents.get(2).getId());
    }
    
}