package hse.zoo.Application.services;

import java.util.Date;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import hse.zoo.Domain.entities.FeedingSchedule;
import hse.zoo.Domain.events.FeedingTimeEvent;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IAnimalRepository;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IFeedingScheduleRepository;
import hse.zoo.Domain.interfaces.servicesInterfaces.IFeedingOrganizationService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeedingOrganizationService implements IFeedingOrganizationService {

    private final ApplicationEventPublisher publisher;

    private final IAnimalRepository animalRepository;

    private final IFeedingScheduleRepository feedingScheduleRepository;

    @Override
    public Integer addFeedingSchedule(FeedingSchedule schedule) {
        return feedingScheduleRepository.addFeedingSchedule(schedule);
    }

    @Override
    public Boolean deleteFeedingSchedule(Integer scheduleId) {
        return feedingScheduleRepository.deleteFeedingSchedule(scheduleId);
    }

    @Override
    public FeedingSchedule changeSchedule(Integer scheduleId, FeedingSchedule newSchedule) {
        return feedingScheduleRepository.changeSchedule(scheduleId, newSchedule);
    }

    @Override
    public FeedingSchedule getSchedule(Integer id) {
        return feedingScheduleRepository.getSchedule(id);
    }

    @Override
    public Boolean checkIfFed(Integer scheduleId) {
        Boolean result = feedingScheduleRepository.checkIfFed(scheduleId);
        FeedingSchedule schedule = getSchedule(scheduleId);
        if (result) {
            publisher.publishEvent(new FeedingTimeEvent(animalRepository.findAnimal(schedule.getAnimal()), schedule.getFoodType(), new Date()));
        }
        return result;
    }
    
}