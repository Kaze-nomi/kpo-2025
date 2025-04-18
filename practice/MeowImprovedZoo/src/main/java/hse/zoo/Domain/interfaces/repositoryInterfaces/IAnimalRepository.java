package hse.zoo.Domain.interfaces.repositoryInterfaces;

import hse.zoo.Domain.entities.Animal;
import java.util.List;

public interface IAnimalRepository {
    Integer addAnimal(Animal animal);
    Animal getAnimal(Integer id);
    Boolean deleteAnimal(Integer id);
    List<Animal> getAllAnimals();
    Integer findAnimal(Animal animal);
}