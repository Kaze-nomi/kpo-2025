package hse.zoo.Domain.entities;

import java.util.List;

import hse.zoo.Domain.valueobjects.AnimalSpecies;
import hse.zoo.Domain.valueobjects.EnclosureSize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Enclosure {

    @Getter
    private List<AnimalSpecies> type; // Sets which species are allowed to live in the enclosure

    @Getter
    private EnclosureSize size;

    @Getter
    private Integer currentAnimalCount = 0;

    @Getter
    private Integer maxAnimalCount;

    public String toString() {
        String species = type.stream().map(AnimalSpecies::getName).reduce((a, b) -> a + ", " + b).get();
        return "Enclosure: " + species + ", size: " + size + ", currentAnimalCount: " + currentAnimalCount + ", maxAnimalCount: " + maxAnimalCount;
    }

    public Enclosure(List<String> type, Integer height, Integer width, Integer length, Integer maxAnimalCount) {
        this.type = type.stream().map(AnimalSpecies::new).toList();
        this.size = new EnclosureSize(length, width, height);
        this.maxAnimalCount = maxAnimalCount;
    }

    public Boolean isAllowed(AnimalSpecies animalSpecies) {
        return type.stream().anyMatch(t -> t.equals(animalSpecies));
    }
    
    public Enclosure addAnimal(Animal animal) {
        if (currentAnimalCount < maxAnimalCount) {
            AnimalSpecies animalSpecies = animal.getSpecies();
            if (isAllowed(animalSpecies)) {
                currentAnimalCount++;
                animal.moveTo(this);
                return this;
            }
            throw new IllegalArgumentException("Animal species " + animalSpecies.getName() + " is not allowed in this enclosure.");
        } else {
            throw new IllegalArgumentException("Enclosure is full.");
        }
    }

    public Enclosure removeAnimal(Animal animal) {
        if (currentAnimalCount > 0) {
            currentAnimalCount--;
            animal.moveTo(null);
            return this;
        }
        throw new IllegalArgumentException("Enclosure is empty.");
    }
    
    public Enclosure clean() {
        System.out.println("Enclosure was cleaned.");
        return this;
    }
}
