package hse.kpo.domains.ships;

import hse.kpo.domains.customers.Customer;
import hse.kpo.interfaces.domainInterfaces.ITransport;
import hse.kpo.interfaces.engineInterfaces.IEngine;
import hse.kpo.params.ProductionTypes;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Ship implements ITransport {

    @Getter
    private IEngine engine;

    @Getter
    private int VIN;

    @Override
    public String getTransportType() {
        return "Ship";
    }

    @Override
    public String getEngineType() {
        return engine.toString();
    }

    @Override
    public int getID() {
        return VIN;
    }

    public Ship(int VIN, IEngine engine) {
        this.VIN = VIN;
        this.engine = engine;
    }

    @Override
    public boolean isCompatible(Customer customer) {
        return this.engine.isCompatible(customer, ProductionTypes.CATAMARAN); // внутри метода просто вызываем соответствующий метод двигателя
    }
}