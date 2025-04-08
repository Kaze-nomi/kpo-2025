package hse.zoo.Domain.events;

import java.util.Date;

import lombok.Getter;

public class AnimalMovedEvent {

    @Getter
    private final Integer animalId;

    @Getter
    private final Integer fromEnclosureId;

    @Getter
    private final Integer toEnclosureId;

    @Getter
    private final Date date;
    
    public AnimalMovedEvent(Integer animalId, Integer fromEnclosureId, Integer toEnclosureId, Date date) {
        this.animalId = animalId;
        this.fromEnclosureId = fromEnclosureId;
        this.toEnclosureId = toEnclosureId;
        this.date = date;
    }
    
}