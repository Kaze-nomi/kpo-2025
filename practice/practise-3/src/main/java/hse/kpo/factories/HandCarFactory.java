<<<<<<< HEAD:practice/practise-3/src/main/java/hse/kpo/factories/HandCarFactory.java
package hse.kpo.factories;

import hse.kpo.domains.Car;
import hse.kpo.domains.HandEngine;
import hse.kpo.interfaces.ICarFactory;
import hse.kpo.params.EmptyEngineParams;
import org.springframework.stereotype.Component;

@Component
public class HandCarFactory implements ICarFactory<EmptyEngineParams> {
<<<<<<< HEAD
=======
=======
/**
 * A factory that creates cars with manual engines.
 *
 */
package studying;

public class HandCarFactory implements ICarFactory<EmptyEngineParams> {
>>>>>>> fa73bc1 (delete)

    /**
     * Creates a car with a manual engine.
     *
     * @param carParams the parameters to pass to the engine
     * @param carNumber the number of the car
     * @return the created car
     */
<<<<<<< HEAD
=======
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/HandCarFactory.java
>>>>>>> fa73bc1 (delete)
    @Override
    public Car createCar(EmptyEngineParams carParams, int carNumber) {
        var engine = new HandEngine(); // Creates a manual engine with no parameters

        return new Car(carNumber, engine); // Creates a car with the manual engine
    }
}

