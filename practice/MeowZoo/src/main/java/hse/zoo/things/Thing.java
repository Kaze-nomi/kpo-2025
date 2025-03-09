package hse.zoo.things;

import hse.zoo.interfaces.IInventory;

public class Thing implements IInventory {
    private int number;
        
    public Thing(int number) {
        setNumber(number);
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
}