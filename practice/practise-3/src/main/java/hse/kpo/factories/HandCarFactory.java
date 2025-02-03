package hse.kpo.factories;

import hse.kpo.domains.Car;
import hse.kpo.domains.HandEngine;
import hse.kpo.interfaces.ICarFactory;
import hse.kpo.params.EmptyEngineParams;
import org.springframework.stereotype.Component;


@Component
public class HandCarFactory implements ICarFactory {
    @Override
    public Car createCar(EmptyEngineParams carParams, int carNumber) {
        var engine = new HandEngine(); // Creates a manual engine with no parameters

        return new Car(carNumber, engine); // Creates a car with the manual engine
    }
}

