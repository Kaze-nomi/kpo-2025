package hse.zoo.animals;

import hse.zoo.interfaces.IAlive;
import hse.zoo.interfaces.IInventory;

public abstract class Animal implements IAlive, IInventory {
    private int food;
    private int number;
    private boolean isHealthy;

    public Animal(int food, int number, boolean isHealthy) {
        setFood(food);
        setNumber(number);
        this.isHealthy = isHealthy;
    }

    @Override
    public int getFood() {
        return food;
    }

    @Override
    public void setFood(int food) {
        checkFood(food);
        this.food = food;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        checkNumber(number);
        this.number = number;
    }

    public boolean isHealthy() {
        return isHealthy;
    }

    public void setHealthy(boolean healthy) {
        isHealthy = healthy;
    }
}
