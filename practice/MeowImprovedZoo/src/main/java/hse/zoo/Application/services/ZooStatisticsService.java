package hse.zoo.Application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import hse.zoo.Domain.entities.Animal;
import hse.zoo.Domain.entities.Enclosure;
import hse.zoo.Domain.entities.FeedingSchedule;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IAnimalRepository;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IEnclosureRepository;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IFeedingScheduleRepository;
import hse.zoo.Domain.interfaces.servicesInterfaces.IZooStatisticsService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ZooStatisticsService implements IZooStatisticsService {

    private final IAnimalRepository animalRepository;

    private final IEnclosureRepository enclosureRepository;

    private final IFeedingScheduleRepository feedingRepository;

    @Override
    public List<Animal> getAllAnimals() {
        return animalRepository.getAllAnimals();
    }

    @Override
    public List<Enclosure> getAllEnclosures() {
        return enclosureRepository.getAllEnclosures();
    }

    @Override
    public List<FeedingSchedule> getAllFeedingSchedules() {
        return feedingRepository.getAllFeedingSchedules();
    }

    @Override
    public List<Animal> getAllAnimalsInEnclosure(Integer enclosureId) {
        Enclosure enclosure = enclosureRepository.getEnclosure(enclosureId);
        List<Animal> animals = getAllAnimals();
        animals.removeIf(animal -> animal.getEnclosure() != enclosure);
        return animals;
    }

    @Override
    public List<Animal> getAllAnimalsWithoutEnclosure() {
        List<Animal> animals = getAllAnimals();
        animals.removeIf(animal -> animal.getEnclosure() != null);
        return animals;
    }

    @Override
    public List<Enclosure> getEmptyEnclosures() {
        List<Enclosure> enclosures = new ArrayList<>();
        for (Enclosure enclosure : enclosureRepository.getAllEnclosures()) {
            if (enclosure.getCurrentAnimalCount() == 0) {
                enclosures.add(enclosure);
            }
        }
        return enclosures;
    }

    @Override
    public List<Animal> getIllAnimals() {
        List<Animal> animals = new ArrayList<>();
        for (Animal animal : animalRepository.getAllAnimals()) {
            if (animal.getIsHealthy() == false) {
                animals.add(animal);
            }
        }
        return animals;
    }
}