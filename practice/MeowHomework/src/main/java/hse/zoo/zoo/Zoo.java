package hse.zoo.zoo;

import hse.zoo.animals.Animal;
import hse.zoo.animals.Herbo;
import hse.zoo.things.Thing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Zoo {
    private List<Animal> animals = new ArrayList<>();
    private List<Thing> things = new ArrayList<>();
    private final VeterinaryClinic veterinaryClinic;

    @Autowired
    public Zoo(VeterinaryClinic veterinaryClinic) {
        this.veterinaryClinic = veterinaryClinic;
    }

    public void addAnimal(Animal animal) {
        if (veterinaryClinic.checkHealth(animal)) {
            animals.add(animal);
        }
    }

    public int calculateTotalFood() {
        int totalFood = 0;
        for (Animal animal : animals) {
            totalFood += animal.getFood();
        }
        return totalFood;
    }

    public List<Animal> getAnimalsForContactZoo() {
        List<Animal> contactZooAnimals = new ArrayList<>();
        for (Animal animal : animals) {
            if (animal instanceof Herbo && ((Herbo) animal).canInteractWithVisitors()) {
                contactZooAnimals.add(animal);
            }
        }
        return contactZooAnimals;
    }

    public void addThing(Thing thing) {
        things.add(thing);
    }

    public void printInventory() {
        for (Animal animal : animals) {
            System.out.println("Animal: " + animal.getClass().getSimpleName() + ", Number: " + animal.getNumber());
        }
        for (Thing thing : things) {
            System.out.println("Thing: " + thing.getClass().getSimpleName() + ", Number: " + thing.getNumber());
        }
    }
}
