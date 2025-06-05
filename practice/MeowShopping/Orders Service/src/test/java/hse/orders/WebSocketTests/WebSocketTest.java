package hse.orders.WebSocketTests;

import hse.orders.domains.Order;
import hse.orders.domains.Progress;
import hse.orders.websocket.OrderNotificationController;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebSocketTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private OrderNotificationController notificationController;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    void notifyOrderUpdate_ValidOrder_SendsToCorrectDestination() {
        // Arrange
        Order order = new Order();
        order.setId(1);
        order.setUserId(100);
        order.setStatus(Progress.NEW);
        String expectedDestination = "/topic/orders/100";

        // Act
        notificationController.notifyOrderUpdate(order);

        // Assert
        verify(messagingTemplate).convertAndSend(
            eq(expectedDestination), 
            orderCaptor.capture()
        );
        
        Order sentOrder = orderCaptor.getValue();
        assertEquals(order.getId(), sentOrder.getId());
        assertEquals(order.getUserId(), sentOrder.getUserId());
        assertEquals(order.getStatus(), sentOrder.getStatus());
    }

    @Test
    void notifyOrderUpdate_DifferentUser_SendsToDifferentDestination() {
        // Arrange
        Order order = new Order();
        order.setId(2);
        order.setUserId(200);
        String expectedDestination = "/topic/orders/200";

        // Act
        notificationController.notifyOrderUpdate(order);

        // Assert
        verify(messagingTemplate).convertAndSend(
            eq(expectedDestination), 
            orderCaptor.capture()
        );
        
        Order sentOrder = orderCaptor.getValue();
        assertEquals(order.getId(), sentOrder.getId());
        assertEquals(order.getUserId(), sentOrder.getUserId());
        assertEquals(order.getStatus(), sentOrder.getStatus());
    }

    @Test
    void notifyOrderUpdate_StatusChange_IncludesStatusInMessage() {
        // Arrange
        Order order = new Order();
        order.setId(3);
        order.setUserId(300);
        order.setStatus(Progress.FINISHED);
        String expectedDestination = "/topic/orders/300";

        // Act
        notificationController.notifyOrderUpdate(order);

        // Assert
        verify(messagingTemplate).convertAndSend(
            eq(expectedDestination), 
            orderCaptor.capture()
        );
        
        assertEquals(Progress.FINISHED, orderCaptor.getValue().getStatus());
    }
    
}