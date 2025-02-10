package hse.zoo.animals;

public class Herbo extends Animal {
    private int kindnessLevel;

    public Herbo(int food, int number, boolean isHealthy, int kindnessLevel) {
        super(food, number, isHealthy);
        setKindnessLevel(kindnessLevel);
    }

    public int getKindnessLevel() {
        return kindnessLevel;
    }

    public void setKindnessLevel(int kindnessLevel) {
        if (kindnessLevel < 0 || kindnessLevel > 10) {
            throw new IllegalArgumentException("Kindness level must be between 0 and 10");
        } 
        this.kindnessLevel = kindnessLevel;
    }

    public boolean canInteractWithVisitors() {
        return kindnessLevel > 5;
    }
}
