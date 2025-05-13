package hse.zoo.Application.eventHandlers;

import hse.zoo.Domain.events.FeedingTimeEvent;

import java.util.Date;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FeedingTimeHandler {

    @EventListener
    public void handleEvent(FeedingTimeEvent event) {
        System.out.println("Обработчик: животное с id " + event.getAnimalId() +
                " поело в " + new Date());
    }
}

