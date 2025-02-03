package hse.kpo.domains;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class Customer {
    @Getter
    private final String name;

    @Getter
    private final int legPower;

    @Getter
    private final int handPower;

    @Getter
    private final int iq;

    @Setter
    private Car car;

    public Customer(String name, int legPower, int handPower, int iq) {
        this.name = name;
        this.legPower = legPower;
        this.handPower = handPower;
        this.iq = iq;
    }
}
