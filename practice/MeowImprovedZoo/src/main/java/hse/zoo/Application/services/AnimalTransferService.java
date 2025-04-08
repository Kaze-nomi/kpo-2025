package hse.zoo.Application.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import java.util.Date;

import hse.zoo.Domain.entities.Animal;
import hse.zoo.Domain.entities.Enclosure;
import hse.zoo.Domain.events.AnimalMovedEvent;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IAnimalRepository;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IEnclosureRepository;
import hse.zoo.Domain.interfaces.servicesInterfaces.IAnimalTransferService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnimalTransferService implements IAnimalTransferService {

    private final ApplicationEventPublisher publisher;

    private final IAnimalRepository animalRepository;

    private final IEnclosureRepository enclosureRepository;

    @Override
    public Integer addAnimal(Animal animal) {
        return animalRepository.addAnimal(animal);
    }

    @Override
    public Boolean deleteAnimal(Integer animalId) {
        return animalRepository.deleteAnimal(animalId);
    }

    @Override
    public Integer addEnclosure(Enclosure enclosure) {
        return enclosureRepository.addEnclosure(enclosure);
    }

    @Override
    public Boolean deleteEnclosure(Integer enclosureId) {
        return enclosureRepository.deleteEnclosure(enclosureId);
    }

    @Override
    public void transferAnimalToEnclosure(Integer animalId, Integer enclosureId) {
        Animal animal = animalRepository.getAnimal(animalId);
        Enclosure currentEnclosure = animal.getEnclosure();
        if (currentEnclosure != null) {
            currentEnclosure.removeAnimal(animal);
        }
        Enclosure newEnclosure = enclosureRepository.getEnclosure(enclosureId);
        newEnclosure.addAnimal(animal);
        publisher.publishEvent(new AnimalMovedEvent(animalId, enclosureRepository.findEnclosure(currentEnclosure), enclosureId, new Date()));
    }

    @Override
    public Integer getEnclsoureByAnimalId(Integer animalId) {
        Animal animal = animalRepository.getAnimal(animalId);
        Enclosure currentEnclosure = animal.getEnclosure();
        if (currentEnclosure == null) {
            return null;
        }
        Integer enclosureId = enclosureRepository.findEnclosure(currentEnclosure);
        return enclosureId;
    }
        
}
