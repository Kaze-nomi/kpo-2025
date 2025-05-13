package hse.zoo.Domain.interfaces.servicesInterfaces;

import java.util.List;

import hse.zoo.Domain.entities.Animal;
import hse.zoo.Domain.entities.Enclosure;
import hse.zoo.Domain.entities.FeedingSchedule;

public interface IZooStatisticsService {

    List<Animal> getAllAnimals();

    List<Enclosure> getAllEnclosures();

    List<FeedingSchedule> getAllFeedingSchedules();

    List<Animal> getAllAnimalsInEnclosure(Integer enclosureId);

    List<Animal> getAllAnimalsWithoutEnclosure();

    List<Enclosure> getEmptyEnclosures();

    List<Animal> getIllAnimals();

}