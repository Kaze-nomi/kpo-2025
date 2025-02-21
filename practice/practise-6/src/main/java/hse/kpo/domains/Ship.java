package hse.kpo.domains;

import hse.kpo.interfaces.IEngine;
import hse.kpo.params.ProductionTypes;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Ship {

    @Getter
    private IEngine engine;

    @Getter
    private int VIN;

    public Ship(int VIN, IEngine engine) {
        this.VIN = VIN;
        this.engine = engine;
    }

    public boolean isCompatible(Customer customer) {
        return this.engine.isCompatible(customer, ProductionTypes.CATAMARAN); // внутри метода просто вызываем соответствующий метод двигателя
    }
}
