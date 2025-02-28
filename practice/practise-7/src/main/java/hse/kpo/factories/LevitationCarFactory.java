package hse.kpo.factories;

import hse.kpo.domains.Car;
import hse.kpo.domains.LevitationEngine;
import hse.kpo.interfaces.ICarFactory;
import hse.kpo.params.EmptyEngineParams;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
/**
 * A class that implements the ICarFactory interface and produces levitation cars
 */
@Component
@RequiredArgsConstructor
public class LevitationCarFactory implements ICarFactory<EmptyEngineParams> {
    /**
     * A method that creates a car
     * @param carNumber the number of the car
     * @return the created car
     */
    @Override
    public Car createCar(EmptyEngineParams carParams, int carNumber) {
        var engine = new LevitationEngine();

        return new Car(carNumber, engine);
    }
}