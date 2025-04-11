package hse.zoo.Application.facade;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import hse.zoo.Domain.entities.Animal;
import hse.zoo.Domain.entities.Enclosure;
import hse.zoo.Domain.entities.FeedingSchedule;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IAnimalRepository;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IEnclosureRepository;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IFeedingScheduleRepository;
import hse.zoo.Domain.interfaces.servicesInterfaces.IAnimalTransferService;
import hse.zoo.Domain.interfaces.servicesInterfaces.IFeedingOrganizationService;
import hse.zoo.Domain.interfaces.servicesInterfaces.IZooStatisticsService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ZooFacade {

    private final IAnimalTransferService animalTransferService;

    private final IFeedingOrganizationService feedingOrganizationService;

    private final IZooStatisticsService zooStatisticsService;

    private final IAnimalRepository animalRepository;

    private final IEnclosureRepository enclosureRepository;

    private final IFeedingScheduleRepository feedingScheduleRepository;

    public Integer addAnimal(Integer enclosureId, String name, Date birthDate, Boolean sex, String favouriteFood, Boolean isHealthy, String species) {
        Integer animalId = animalTransferService.addAnimal(new Animal(species, name, birthDate, sex, favouriteFood, isHealthy));
        if (enclosureId != null) {
            animalTransferService.transferAnimalToEnclosure(animalId, enclosureId);
        }
        return animalId;
    }
    
    public Boolean deleteAnimal(Integer animalId) {
        return animalTransferService.deleteAnimal(animalId);
    }

    public Integer addEnclosure(List<String> species, Integer width, Integer height, Integer length, Integer maxAnimalCount) {
        Enclosure enclosure = new Enclosure(species, width, height, length, maxAnimalCount);
        Integer enclosureId = animalTransferService.addEnclosure(enclosure);
        return enclosureId;
    }

    public Boolean deleteEnclosure(Integer enclosureId) {
        return animalTransferService.deleteEnclosure(enclosureId);
    }

    public void transferAnimalToEnclosure(Integer animalId, Integer enclosureId) {
        animalTransferService.transferAnimalToEnclosure(animalId, enclosureId);
    }

    public Integer addFeedingSchedule(Integer animalId, Time feedingTime, Boolean foodType) {
        Animal animal = animalRepository.getAnimal(animalId);
        FeedingSchedule schedule = new FeedingSchedule(animal, feedingTime, foodType);
        return feedingOrganizationService.addFeedingSchedule(schedule);
    }

    public Boolean deleteFeedingSchedule(Integer scheduleId) {
        return feedingOrganizationService.deleteFeedingSchedule(scheduleId);
    }

    public void changeSchedule(Integer scheduleId, Integer animalId, Time feedingTime, Boolean foodType) {
        FeedingSchedule schedule = new FeedingSchedule(animalRepository.getAnimal(animalId), feedingTime, foodType);
        feedingOrganizationService.changeSchedule(scheduleId, schedule);
    }

    public FeedingSchedule getSchedule(Integer scheduleId) {
        return feedingOrganizationService.getSchedule(scheduleId);
    }

    public List<FeedingSchedule> getSchedules() {
        return zooStatisticsService.getAllFeedingSchedules();
    }

    public Animal getAnimal(Integer animalId) {
        return animalRepository.getAnimal(animalId);
    }

    public List<Animal> getAnimals(){
        return zooStatisticsService.getAllAnimals();
    }

    public Enclosure getEnclosure(Integer enclosureId) {
        return enclosureRepository.getEnclosure(enclosureId);
    }

    public List<Enclosure> getEnclosures() {
        return zooStatisticsService.getAllEnclosures();
    }

    public List<Animal> getAnimalsInEnclosure(Integer enclosureId) {
        return zooStatisticsService.getAllAnimalsInEnclosure(enclosureId);
    }

    public List<Animal> getAnimalsWithoutEnclosure() {
        return zooStatisticsService.getAllAnimalsWithoutEnclosure();
    }

    public List<Enclosure> getEmptyEnclosures() {
        return zooStatisticsService.getEmptyEnclosures();
    }

    public List<Animal> getIllAnimals() {
        return zooStatisticsService.getIllAnimals();
    }
    
    public void healAnimal(Integer animalId) {
        animalRepository.getAnimal(animalId).heal();
    }

    public Integer getEnclosureByAnimalId(Integer animalId) {
        return animalTransferService.getEnclsoureByAnimalId(animalId);
    }

    public Boolean checkIfFed(Integer animalId) {
        return feedingOrganizationService.checkIfFed(animalId);
    }

    public Integer getAnimalId(Animal animal) {
        return animalRepository.findAnimal(animal);
    }

    public Integer getEnclosureId(Enclosure enclosure) {
        return enclosureRepository.findEnclosure(enclosure);
    }

    public Integer getFeedingScheduleId(FeedingSchedule schedule) {
        return feedingScheduleRepository.findScheduleId(schedule);
    }

}
