package hse.zoo.Domain.entities;

import java.sql.Time;
import java.time.LocalTime;
import java.time.ZoneId;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class FeedingSchedule {

    @Getter
    private Animal animal;

    @Getter
    private Time feedingTime;

    @Getter
    private Boolean foodType; // 0 - vegetables, 1 - meat

    public FeedingSchedule(Animal animal, Time feedingTime, Boolean foodType) {
        this.animal = animal;
        this.feedingTime = feedingTime;
        this.foodType = foodType;
    }

    public FeedingSchedule changeSchedule(Animal animal, Time feedingTime, Boolean foodType) {
        this.animal = animal;
        this.feedingTime = feedingTime;
        this.foodType = foodType;
        return this;
    }

    public Boolean checkIfFed() {
        LocalTime feedingTimeOfDay = feedingTime.toLocalTime();
        LocalTime currentTime = LocalTime.now(ZoneId.systemDefault());
        if (feedingTimeOfDay.isBefore(currentTime)) {
            animal.feed();
            return true;
        } else {
            System.out.println("Животное ещё не поело.");
        }
        return false;
    }
}