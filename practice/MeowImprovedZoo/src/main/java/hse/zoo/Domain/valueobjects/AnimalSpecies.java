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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AnimalSpecies that = (AnimalSpecies) obj;
        return name.equals(that.name);
    }

}