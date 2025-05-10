package hse.zoo.Domain.interfaces.servicesInterfaces;

import hse.zoo.Domain.entities.FeedingSchedule;

public interface IFeedingOrganizationService {

    Integer addFeedingSchedule(FeedingSchedule schedule);

    Boolean deleteFeedingSchedule(Integer scheduleId);

    FeedingSchedule changeSchedule(Integer scheduleId, FeedingSchedule newSchedule);

    Boolean checkIfFed(Integer scheduleId);

    FeedingSchedule getSchedule(Integer id);
    
}