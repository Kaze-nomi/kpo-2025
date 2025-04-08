package hse.zoo.Domain.interfaces.servicesInterfaces;

import hse.zoo.Domain.entities.Animal;
import hse.zoo.Domain.entities.Enclosure;

public interface IAnimalTransferService {

    Integer addAnimal(Animal animal);

    Boolean deleteAnimal(Integer animalId);

    Integer addEnclosure(Enclosure enclosure);

    Boolean deleteEnclosure(Integer enclosureId);

    void transferAnimalToEnclosure(Integer animalId, Integer enclosureId);

    Integer getEnclsoureByAnimalId(Integer animalId);

}
