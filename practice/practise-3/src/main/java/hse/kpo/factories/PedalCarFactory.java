package hse.kpo.factories;

<<<<<<< HEAD:practice/practise-3/src/main/java/hse/kpo/factories/PedalCarFactory.java

import hse.kpo.domains.Car;
import hse.kpo.domains.PedalEngine;
import hse.kpo.interfaces.ICarFactory;
import hse.kpo.params.PedalEngineParams;

public class PedalCarFactory implements ICarFactory<PedalEngineParams> {
=======
/**
 * A factory that creates cars with pedal engines.
 */
public class PedalCarFactory implements ICarFactory<PedalEngineParams>{

    /**
     * Creates a car with a pedal engine.
     *
     * @param carParams the parameters to configure the pedal engine
     * @param carNumber the identification number of the car
     * @return the created car with the specified pedal engine and number
     */
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/PedalCarFactory.java
    @Override
    public Car createCar(PedalEngineParams carParams, int carNumber) {
        var engine = new PedalEngine(carParams.pedalSize());

        return new Car(carNumber, engine);
    }
}

