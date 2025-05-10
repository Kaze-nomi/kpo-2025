package hse.zoo.Infrastructure.repositories;

import hse.zoo.Domain.entities.FeedingSchedule;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IFeedingScheduleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class FeedingScheduleRepository implements IFeedingScheduleRepository {

    private Integer id_counter = 0;

    private final Map<Integer, FeedingSchedule> schedules = new ConcurrentHashMap<>();

    @Override
    public FeedingSchedule getFeedingSchedule(Integer id) {
        if (!schedules.containsKey(id)) {
            throw new IllegalArgumentException("FeedingSchedule with id " + id + " not found");
        }
        return schedules.get(id);
    }

    @Override
    public Integer addFeedingSchedule(FeedingSchedule schedule) {
        schedules.put(id_counter++, schedule);
        return id_counter - 1;
    }

    @Override
    public Boolean deleteFeedingSchedule(Integer id) {
        if (!schedules.containsKey(id)) {
            throw new IllegalArgumentException("FeedingSchedule with id " + id + " not found");
        }
        return schedules.remove(id, schedules.get(id));
    }

    @Override
    public List<FeedingSchedule> getAllFeedingSchedules() {
        return new ArrayList<>(schedules.values());
    }

    @Override
    public Boolean checkIfFed(Integer id) {
        if (!schedules.containsKey(id)) {
            throw new IllegalArgumentException("FeedingSchedule with id " + id + " not found");
        }
        return schedules.get(id).checkIfFed();
    }

    @Override
    public FeedingSchedule changeSchedule(Integer scheduleId, FeedingSchedule newSchedule) {
        if (!schedules.containsKey(scheduleId)) {
            throw new IllegalArgumentException("FeedingSchedule with id " + scheduleId + " not found");
        }
        schedules.get(scheduleId).changeSchedule(newSchedule.getAnimal(), newSchedule.getFeedingTime(), newSchedule.getFoodType());
        return newSchedule;
    }

    @Override
    public FeedingSchedule getSchedule(Integer id) {
        if (!schedules.containsKey(id)) {
            throw new IllegalArgumentException("FeedingSchedule with id " + id + " not found");
        }
        return schedules.get(id);
    }


    @Override
    public Integer findScheduleId(FeedingSchedule schedule) {
        for (Map.Entry<Integer, FeedingSchedule> entry : schedules.entrySet()) {
            if (entry.getValue().equals(schedule)) {
                return entry.getKey();
            }
        }
        return null;
    }

}