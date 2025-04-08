package hse.zoo.Domain.events;

import java.util.Date;

import lombok.Getter;

public class FeedingTimeEvent {

    @Getter
    private final Integer animalId;

    @Getter
    private final Boolean type;

    @Getter
    private final Date date;
    
    public FeedingTimeEvent(Integer animalId, Boolean type, Date date) {
        this.animalId = animalId;
        this.type = type;
        this.date = date;
    }
    
}