package hse.zoo.interfaces;

public interface IInventory {
    int getNumber();
    void setNumber(int number);
    default void checkNumber(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number should be positive");
        }
    }
}
