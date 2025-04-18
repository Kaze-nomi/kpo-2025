package hse.zoo.Domain.entities;

import java.util.Date;

import hse.zoo.Domain.valueobjects.FavouriteFood;
import hse.zoo.Domain.valueobjects.AnimalSpecies;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Animal {

    @Getter
    private Enclosure enclosure;

    @Getter
    private String name;

    @Getter
    private Date birthDate;

    @Getter
    private Boolean sex; // 0 - male, 1 - female

    @Getter
    private FavouriteFood favouriteFood;

    @Getter
    private Boolean isHealthy;

    @Getter
    private AnimalSpecies species;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Animal{");
        sb.append("name='").append(name).append('\'');
        sb.append(", birthDate=").append(birthDate);
        sb.append(", sex=").append(sex);
        sb.append(", favouriteFood=").append(favouriteFood.getFood());
        sb.append(", isHealthy=").append(isHealthy);
        sb.append(", species=").append(species.getName());
        sb.append('}');
        return sb.toString();
    }

    public Animal(String species, String name, Date birthDate, Boolean sex, String favouriteFood, Boolean isHealthy) {
        this.species = new AnimalSpecies(species);
        this.name = name;
        this.birthDate = birthDate;
        this.sex = sex;
        this.favouriteFood = new FavouriteFood(favouriteFood);
        this.isHealthy = isHealthy;
    }
    
    public Animal feed() {
        System.out.println("Животное поело.");
        return this;
    }

    public Animal heal() {
        this.isHealthy = true;
        return this;
    }

    public Animal moveTo(Enclosure enclosure) {
        this.enclosure = enclosure;
        return this;
    }
}