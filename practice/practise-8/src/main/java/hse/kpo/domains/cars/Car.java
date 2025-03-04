package hse.kpo.domains.cars;

import hse.kpo.domains.customers.Customer;
import hse.kpo.interfaces.domainInterfaces.ITransport;
import hse.kpo.interfaces.engineInterfaces.IEngine;
import hse.kpo.params.ProductionTypes;
import lombok.Getter;
import lombok.ToString;

@ToString
public class Car implements ITransport {
    @Getter
    private IEngine engine;
    
    @Getter
    private int VIN;

    @Override
    public String getTransportType() {
        return "Car";
    }

    @Override
    public String getEngineType() {
        return engine.toString();
    }

    @Override
    public int getID() {
        return VIN;
    }

    public Car(int VIN, IEngine engine) {
        this.VIN = VIN;
        this.engine = engine;
    }

    @Override
    public boolean isCompatible(Customer customer) {
        return this.engine.isCompatible(customer, ProductionTypes.CAR); // внутри метода просто вызываем соответствующий метод двигателя
    }
}
