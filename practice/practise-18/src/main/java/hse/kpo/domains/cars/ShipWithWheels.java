package hse.kpo.domains.cars;

import hse.kpo.domains.customers.Customer;
import hse.kpo.interfaces.engineInterfaces.AbstractEngine;
import hse.kpo.params.ProductionTypes;

public class ShipWithWheels extends Car {

    @Override
    public String toString() {
        return String.format("ShipWithWheels(engine=%s, VIN=%d)", this.getEngine().toString(), this.getVIN());
    }


    public ShipWithWheels(int VIN, AbstractEngine engine) {
        super(VIN, engine);
    }

    @Override
    public boolean isCompatible(Customer customer) {
        return this.getEngine().isCompatible(customer, ProductionTypes.CATAMARAN);
    }

    @Override
    public String getTransportType() {
        return "ShipWithWheels";
    }
}