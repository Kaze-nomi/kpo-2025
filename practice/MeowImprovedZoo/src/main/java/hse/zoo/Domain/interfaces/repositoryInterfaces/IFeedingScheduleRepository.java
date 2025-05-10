package hse.zoo.Domain.interfaces.repositoryInterfaces;

import hse.zoo.Domain.entities.FeedingSchedule;
import java.util.List;

public interface IFeedingScheduleRepository {
    Integer addFeedingSchedule(FeedingSchedule schedule);
    Boolean deleteFeedingSchedule(Integer id);
    FeedingSchedule getFeedingSchedule(Integer id);
    List<FeedingSchedule> getAllFeedingSchedules();
    Boolean checkIfFed(Integer id);
    FeedingSchedule changeSchedule(Integer scheduleId, FeedingSchedule newSchedule);
    FeedingSchedule getSchedule(Integer id);
    Integer findScheduleId(FeedingSchedule schedule);
}
