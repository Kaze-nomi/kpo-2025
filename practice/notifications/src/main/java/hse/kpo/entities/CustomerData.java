package hse.kpo.entities;

import lombok.Getter;

public record CustomerData(
    @Getter
    String name,
    @Getter
    int legPower,
    @Getter
    int handPower,
    @Getter
    int iq,
    @Getter
    int carsCount,
    @Getter
    int catamaransCount,
    boolean isValid
) {
    public static final CustomerData INVALID = new CustomerData(
        null, 0, 0, 0, 0, 0, false
    );

    public static CustomerData createValid(String name, int legPower,
                                           int handPower, int iq,
                                           int cars, int catamarans) {
        return new CustomerData(
            name,
            legPower,
            handPower,
            iq,
            cars,
            catamarans,
            name != null &&
                legPower > 0 && legPower < 1000 &&
                handPower > 0 && handPower < 1000 &&
                iq > 0 && iq < 300
        );
    }
}
