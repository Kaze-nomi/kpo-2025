package hse.orders.websocket;

import hse.orders.domains.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class OrderNotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyOrderUpdate(Order order) {
        String destination = "/topic/orders/" + order.getUserId();
        messagingTemplate.convertAndSend(destination, order);
    }
}