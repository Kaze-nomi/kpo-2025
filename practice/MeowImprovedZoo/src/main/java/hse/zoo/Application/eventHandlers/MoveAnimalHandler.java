package hse.zoo.Application.eventHandlers;

import hse.zoo.Domain.events.AnimalMovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoveAnimalHandler {

    @EventListener
    public void handleEvent(AnimalMovedEvent event) {
        System.out.println("Обработчик: животное с id " + event.getAnimalId() +
                " перемещёно из вольера с id " + event.getFromEnclosureId() +
                " в вольер с id " + event.getToEnclosureId() +
                " в " + event.getDate());
    }

}