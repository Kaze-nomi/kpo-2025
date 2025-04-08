package hse.zoo.Domain.entities;

import java.util.Date;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class FeedingSchedule {

    @Getter
    private Animal animal;

    @Getter
    private Date feedingTime;

    @Getter
    private Boolean foodType; // 0 - vegetables, 1 - meat

    public FeedingSchedule(Animal animal, Date feedingTime, Boolean foodType) {
        this.animal = animal;
        this.feedingTime = feedingTime;
        this.foodType = foodType;
    }

    public FeedingSchedule changeSchedule(Animal animal, Date feedingTime, Boolean foodType) {
        this.animal = animal;
        this.feedingTime = feedingTime;
        this.foodType = foodType;
        return this;
    }

    public Boolean checkIfFed() {
        if (feedingTime.before(new Date())) {
            animal.feed();
            return true;
        } else {
            System.out.println("Животное ещё не поело.");
        }
        return false;
    }
}