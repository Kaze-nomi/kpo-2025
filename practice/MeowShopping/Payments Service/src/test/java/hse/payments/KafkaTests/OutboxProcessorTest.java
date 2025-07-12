package hse.payments.KafkaTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hse.payments.kafka.KafkaProducerService;
import hse.payments.kafka.events.OrderProcessedEvent;
import hse.payments.kafka.events.OutboxEvent;
import hse.payments.kafka.outbox.OutboxEventRepository;
import hse.payments.kafka.outbox.OutboxProcessor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxProcessorTest {

    @Mock
    private OutboxEventRepository repository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxProcessor outboxProcessor;

    @Captor
    private ArgumentCaptor<OutboxEvent> outboxEventCaptor;

    @Captor
    private ArgumentCaptor<OrderProcessedEvent> eventCaptor;

    private final Long eventId1 = 1L;
    private final Long eventId2 = 2L;
    private final int orderId1 = 100;
    private final int orderId2 = 200;
    private final boolean success = true;

    private OutboxEvent createOutboxEvent(Long id, int orderId, boolean sent) {
        OutboxEvent event = new OutboxEvent();
        event.setId(id);
        event.setEventType("Заказ обработан");
        event.setPayload(String.format("{\"orderId\":%d,\"isSuccess\":%b}", orderId, true));
        event.setSent(sent);
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }

    @Test
    void processOutboxEvents_SuccessfulProcessing() throws Exception {
        // Arrange
        List<OutboxEvent> events = Arrays.asList(
                createOutboxEvent(eventId1, orderId1, false),
                createOutboxEvent(eventId2, orderId2, false)
        );
        
        when(repository.findAllBySentFalseOrderByCreatedAtAsc())
                .thenReturn(events);
        
        when(objectMapper.readValue(events.get(0).getPayload(), OrderProcessedEvent.class))
                .thenReturn(new OrderProcessedEvent(orderId1, success));
        
        when(objectMapper.readValue(events.get(1).getPayload(), OrderProcessedEvent.class))
                .thenReturn(new OrderProcessedEvent(orderId2, !success));

        // Act
        outboxProcessor.processOutboxEvents();

        // Assert
        verify(kafkaProducerService, times(2)).returnToSender(eventCaptor.capture());
        
        List<OrderProcessedEvent> sentEvents = eventCaptor.getAllValues();
        assertEquals(2, sentEvents.size());
        assertEquals(orderId1, sentEvents.get(0).orderId());
        assertEquals(success, sentEvents.get(0).isSuccess());
        assertEquals(orderId2, sentEvents.get(1).orderId());
        assertEquals(!success, sentEvents.get(1).isSuccess());
        
        verify(repository, times(2)).save(outboxEventCaptor.capture());
        
        List<OutboxEvent> updatedEvents = outboxEventCaptor.getAllValues();
        assertTrue(updatedEvents.get(0).isSent());
        assertTrue(updatedEvents.get(1).isSent());
    }

    @Test
    void processOutboxEvents_EmptyQueue_NoProcessing() {
        // Arrange
        when(repository.findAllBySentFalseOrderByCreatedAtAsc())
                .thenReturn(List.of());

        // Act
        outboxProcessor.processOutboxEvents();

        // Assert
        verifyNoInteractions(kafkaProducerService);
        verifyNoInteractions(objectMapper);
        verify(repository, never()).save(any());
    }

    @Test
    void processOutboxEvents_DeserializationFailure_ThrowsException() throws Exception {
        // Arrange
        OutboxEvent event = createOutboxEvent(eventId1, orderId1, false);
        when(repository.findAllBySentFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(event));
        
        when(objectMapper.readValue(event.getPayload(), OrderProcessedEvent.class))
                .thenThrow(new JsonProcessingException("Deserialization error") {});

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> outboxProcessor.processOutboxEvents()
        );
        
        verify(kafkaProducerService, never()).returnToSender(any());
        verify(repository, never()).save(any());
    }

    @Test
    void processOutboxEvents_SendFailure_EventNotMarkedAsSent() throws Exception {
        // Arrange
        OutboxEvent event = createOutboxEvent(eventId1, orderId1, false);
        when(repository.findAllBySentFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(event));
        
        when(objectMapper.readValue(event.getPayload(), OrderProcessedEvent.class))
                .thenReturn(new OrderProcessedEvent(orderId1, success));
        
        doThrow(new RuntimeException("Kafka error"))
                .when(kafkaProducerService).returnToSender(any());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> outboxProcessor.processOutboxEvents()
        );
        
        verify(repository, never()).save(any());
    }

    @Test
    void processOutboxEvents_PartialProcessing() throws Exception {
        // Arrange
        OutboxEvent successEvent = createOutboxEvent(eventId1, orderId1, false);
        OutboxEvent failEvent = createOutboxEvent(eventId2, orderId2, false);
        
        when(repository.findAllBySentFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(successEvent, failEvent));
        
        when(objectMapper.readValue(successEvent.getPayload(), OrderProcessedEvent.class))
                .thenReturn(new OrderProcessedEvent(orderId1, success));
        
        when(objectMapper.readValue(failEvent.getPayload(), OrderProcessedEvent.class))
                .thenThrow(new JsonProcessingException("Error") {});

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> outboxProcessor.processOutboxEvents()
        );
        
        verify(kafkaProducerService, times(1)).returnToSender(any());
        verify(repository, times(1)).save(outboxEventCaptor.capture());
    }
    
}