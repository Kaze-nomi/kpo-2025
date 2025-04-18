package hse.zoo.Domain.valueobjects;

import lombok.Getter;

public class FavouriteFood {
    
    @Getter
    private String food;
    
    public FavouriteFood(String food) {
        if (food.matches(".*\\d.*") || food.isEmpty()) {
            throw new IllegalArgumentException("Invalid food name: " + food);
        } else {
            this.food = food;
        }
    }
}
