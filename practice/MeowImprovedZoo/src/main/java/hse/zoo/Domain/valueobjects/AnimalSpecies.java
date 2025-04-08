package hse.zoo.Domain.valueobjects;

import lombok.Getter;

public class AnimalSpecies {

    @Getter
    private String name;

    public AnimalSpecies(String name) {
        if (name.matches(".*\\d.*") || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid species name: " + name);
        } else {
            this.name = name;
        }
    }
}
