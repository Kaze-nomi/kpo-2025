package hse.zoo.interfaces;

public interface IAlive {
    int getFood();
    void setFood(int food);
    default void checkFood(int food) {
        if (food < 0) {
            throw new IllegalArgumentException("Food should be positive");
        }
    }
}
