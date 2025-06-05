package hse.payments.KafkaTests;

import hse.payments.kafka.events.InboxEvent;
import hse.payments.kafka.events.OrderAddedEvent;
import hse.payments.kafka.events.OrderProcessedEvent;
import hse.payments.kafka.events.OutboxEvent;
import hse.payments.kafka.inbox.InboxEventRepository;
import hse.payments.kafka.inbox.InboxProcessor;
import hse.payments.kafka.outbox.OutboxEventRepository;
import hse.payments.services.PaymentsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboxProcessorTest {

    @Mock
    private PaymentsService paymentsService;

    @Mock
    private InboxEventRepository inboxRepository;

    @Mock
    private OutboxEventRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private InboxProcessor inboxProcessor;

    @Captor
    private ArgumentCaptor<OutboxEvent> outboxEventCaptor;

    private final int userId = 123;
    private final int orderId = 456;
    private final double amount = 50.0;
    private final String description = "Test order";

    @BeforeEach
    void setUp() {
        lenient().when(inboxRepository.findAllByProcessedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(
                        createInboxEvent(1L, false),
                        createInboxEvent(2L, false)));
    }

    private InboxEvent createInboxEvent(long id, boolean processed) {
        InboxEvent event = new InboxEvent();
        event.setId(id);
        event.setPayload("payload-" + id);
        event.setProcessed(processed);
        return event;
    }

    @Test
    void processInboxEvents_SuccessfulWithdrawal() throws Exception {
        // Arrange
        OrderAddedEvent orderEvent = new OrderAddedEvent(orderId, userId, amount, description);
        when(objectMapper.readValue(anyString(), eq(OrderAddedEvent.class)))
                .thenReturn(orderEvent);

        when(paymentsService.withdraw(userId, amount)).thenReturn(true);

        when(objectMapper.writeValueAsString(any(OrderProcessedEvent.class)))
                .thenReturn("{\"orderId\":" + orderId + ",\"success\":true}");

        // Act
        inboxProcessor.processInboxEvents();

        // Assert
        verify(outboxRepository, times(2)).save(outboxEventCaptor.capture());

        List<OutboxEvent> savedEvents = outboxEventCaptor.getAllValues();
        assertEquals(2, savedEvents.size());

        for (OutboxEvent event : savedEvents) {
            assertEquals("Заказ обработан", event.getEventType());
            assertFalse(event.isSent());
            assertNotNull(event.getCreatedAt());
            assertTrue(event.getPayload().contains("\"success\":true"));
        }

        verify(inboxRepository, times(2)).save(argThat(e -> e.isProcessed()));
    }

    @Test
    void processInboxEvents_FailedWithdrawal() throws Exception {
        // Arrange
        OrderAddedEvent orderEvent = new OrderAddedEvent(orderId, userId, amount, description);
        when(objectMapper.readValue(anyString(), eq(OrderAddedEvent.class)))
                .thenReturn(orderEvent);

        when(paymentsService.withdraw(userId, amount)).thenReturn(false);

        when(objectMapper.writeValueAsString(any(OrderProcessedEvent.class)))
                .thenReturn("{\"orderId\":" + orderId + ",\"success\":false}");

        // Act
        inboxProcessor.processInboxEvents();

        // Assert
        verify(outboxRepository, times(2)).save(outboxEventCaptor.capture());

        List<OutboxEvent> savedEvents = outboxEventCaptor.getAllValues();
        for (OutboxEvent event : savedEvents) {
            assertNotNull(event.getPayload()); // Ensure payload is not null
            assertTrue(event.getPayload().contains("\"success\":false"));
        }
    }

    @Test
    void processInboxEvents_MultipleEventsMixedResults() throws Exception {
        // Arrange
        OrderAddedEvent successEvent = new OrderAddedEvent(1, userId, 30.0, "Success");
        OrderAddedEvent failEvent = new OrderAddedEvent(2, userId, 1000.0, "Fail");

        when(objectMapper.readValue("payload-1", OrderAddedEvent.class))
                .thenReturn(successEvent);
        when(objectMapper.readValue("payload-2", OrderAddedEvent.class))
                .thenReturn(failEvent);

        lenient().when(objectMapper.writeValueAsString(any(OrderProcessedEvent.class)))
                .thenAnswer(inv -> {
                    OrderProcessedEvent event = inv.getArgument(0);
                    return "{\"success\":" + event.isSuccess() + "}";
                });

        when(paymentsService.withdraw(userId, 30.0)).thenReturn(true);
        when(paymentsService.withdraw(userId, 1000.0)).thenReturn(false);

        // Act
        inboxProcessor.processInboxEvents();

        // Assert
        verify(outboxRepository, times(2)).save(outboxEventCaptor.capture());

        List<OutboxEvent> savedEvents = outboxEventCaptor.getAllValues();
        assertTrue(savedEvents.get(0).getPayload().contains("\"success\":true"));
        assertTrue(savedEvents.get(1).getPayload().contains("\"success\":false"));
    }

    @Test
    void processInboxEvents_DeserializationFailure() throws Exception {
        // Arrange
        when(objectMapper.readValue(anyString(), eq(OrderAddedEvent.class)))
                .thenThrow(new JsonProcessingException("Invalid JSON") {
                });

        // Act & Assert
        assertThrows(RuntimeException.class, () -> inboxProcessor.processInboxEvents());

        verify(outboxRepository, never()).save(any());
        verify(inboxRepository, never()).save(any());
    }

    @Test
    void processInboxEvents_PaymentServiceException() throws Exception {
        // Arrange
        OrderAddedEvent orderEvent = new OrderAddedEvent(orderId, userId, amount, description);
        when(objectMapper.readValue(anyString(), eq(OrderAddedEvent.class)))
                .thenReturn(orderEvent);

        when(paymentsService.withdraw(userId, amount))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> inboxProcessor.processInboxEvents());

        verify(outboxRepository, never()).save(any());
        verify(inboxRepository, never()).save(any());
    }

    @Test
    void processInboxEvents_SerializationFailure() throws Exception {
        // Arrange
        OrderAddedEvent orderEvent = new OrderAddedEvent(orderId, userId, amount, description);
        when(objectMapper.readValue(anyString(), eq(OrderAddedEvent.class)))
                .thenReturn(orderEvent);

        when(paymentsService.withdraw(userId, amount)).thenReturn(true);
        when(objectMapper.writeValueAsString(any(OrderProcessedEvent.class)))
                .thenThrow(new JsonProcessingException("Serialization error") {
                });

        // Act & Assert
        assertThrows(RuntimeException.class, () -> inboxProcessor.processInboxEvents());

        verify(outboxRepository, never()).save(any());
        verify(inboxRepository, never()).save(any());
    }

    @Test
    void processInboxEvents_NoEventsToProcess() {
        // Arrange
        when(inboxRepository.findAllByProcessedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of());

        // Act
        inboxProcessor.processInboxEvents();

        // Assert
        verifyNoInteractions(objectMapper);
        verifyNoInteractions(paymentsService);
        verifyNoInteractions(outboxRepository);
    }

    @Test
    void processInboxEvents_PartialProcessing() throws Exception {
        // Arrange
        InboxEvent event1 = createInboxEvent(1L, false);
        InboxEvent event2 = createInboxEvent(2L, false);

        when(inboxRepository.findAllByProcessedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(event1, event2));

        OrderAddedEvent event1Data = new OrderAddedEvent(1, userId, 10.0, "First");
        OrderAddedEvent event2Data = new OrderAddedEvent(2, userId, 20.0, "Second");

        when(objectMapper.readValue("payload-1", OrderAddedEvent.class))
                .thenReturn(event1Data);
        when(objectMapper.readValue("payload-2", OrderAddedEvent.class))
                .thenReturn(event2Data);

        when(paymentsService.withdraw(userId, 10.0)).thenReturn(true);
        when(paymentsService.withdraw(userId, 20.0))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> inboxProcessor.processInboxEvents());

        // Проверяем, что первое событие обработано, второе - нет
        verify(inboxRepository).save(argThat(e -> e.getId() == 1L && e.isProcessed()));
        verify(inboxRepository, never()).save(argThat(e -> e.getId() == 2L));
    }

    @Test
    void processInboxEvents_OutboxSaveFailure() throws Exception {
        // Arrange
        OrderAddedEvent orderEvent = new OrderAddedEvent(orderId, userId, amount, description);
        when(objectMapper.readValue(anyString(), eq(OrderAddedEvent.class)))
                .thenReturn(orderEvent);

        when(paymentsService.withdraw(userId, amount)).thenReturn(true);
        when(outboxRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> inboxProcessor.processInboxEvents());

        // Проверяем, что событие не помечено как обработанное
        verify(inboxRepository, never()).save(any());
    }

    @Test
    void processInboxEvents_CorrectOutboxPayload() throws Exception {
        // Arrange
        OrderAddedEvent orderEvent = new OrderAddedEvent(orderId, userId, amount, description);
        when(objectMapper.readValue(anyString(), eq(OrderAddedEvent.class)))
                .thenReturn(orderEvent);

        when(paymentsService.withdraw(userId, amount)).thenReturn(true);
        when(objectMapper.writeValueAsString(any(OrderProcessedEvent.class)))
                .thenAnswer(inv -> {
                    OrderProcessedEvent event = inv.getArgument(0);
                    return "{\"orderId\":" + event.orderId() + ",\"success\":" + event.isSuccess() + "}";
                });

        // Act
        inboxProcessor.processInboxEvents();

        // Assert
        verify(outboxRepository, times(2)).save(outboxEventCaptor.capture());

        OutboxEvent savedEvent = outboxEventCaptor.getValue();
        assertTrue(savedEvent.getPayload().contains("\"orderId\":" + orderId));
        assertTrue(savedEvent.getPayload().contains("\"success\":true"));
        assertEquals("Заказ обработан", savedEvent.getEventType());
        assertFalse(savedEvent.isSent());
        assertNotNull(savedEvent.getCreatedAt());
    }
    
}