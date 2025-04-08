package hse.zoo.Infrastructure.repositories;

import hse.zoo.Domain.entities.Animal;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IAnimalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class AnimalRepository implements IAnimalRepository {

    private Integer id_counter = 0;

    private final Map<Integer, Animal> animals = new ConcurrentHashMap<>();

    @Override
    public Animal getAnimal(Integer id) {
        if (!animals.containsKey(id)) {
            throw new IllegalArgumentException("Animal with id " + id + " not found");
        }
        return animals.get(id);
    }

    @Override
    public Integer addAnimal(Animal animal) {
        animals.put(id_counter++, animal);
        return id_counter - 1;
    }

    @Override
    public Boolean deleteAnimal(Integer id) {
        if (!animals.containsKey(id)) {
            throw new IllegalArgumentException("Animal with id " + id + " not found");
        }
        return animals.remove(id, animals.get(id));
    }

    @Override
    public List<Animal> getAllAnimals() {
        return new ArrayList<>(animals.values());
    }

    @Override
    public Integer findAnimal(Animal animal) {
        for (Map.Entry<Integer, Animal> entry : animals.entrySet()) {
            if (entry.getValue().equals(animal)) {
                return entry.getKey();
            }
        }
        return null;
    }

}