package hse.kpo.domains.engines;

import hse.kpo.domains.customers.Customer;
import hse.kpo.interfaces.engineInterfaces.IEngine;
import hse.kpo.params.ProductionTypes;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class PedalEngine implements IEngine {
    private final int size;

    @Override
    public boolean isCompatible(Customer customer, ProductionTypes type) {
        return switch (type) {
            case ProductionTypes.CAR -> customer.getLegPower() > 5;
            case ProductionTypes.CATAMARAN -> customer.getLegPower() > 2;
            case null, default -> throw new RuntimeException("This type of production doesn't exist");
        };
    }

    public PedalEngine(int size) {
        this.size = size;
    }
}
