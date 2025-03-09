package hse.zoo.zoo;

import hse.zoo.animals.Animal;

import org.springframework.stereotype.Component;

@Component
public class VeterinaryClinic {
    public boolean checkHealth(Animal animal) {
        return animal.isHealthy();
    }
}