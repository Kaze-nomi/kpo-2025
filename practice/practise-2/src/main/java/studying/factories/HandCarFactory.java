<<<<<<< HEAD:practice/practise-2/src/main/java/studying/factories/HandCarFactory.java
package studying.factories;

import studying.domains.Car;
import studying.domains.HandEngine;
import studying.interfaces.ICarFactory;
import studying.params.EmptyEngineParams;

public class HandCarFactory implements ICarFactory<EmptyEngineParams> {
=======
/**
 * A factory that creates cars with manual engines.
 *
 */
package studying;

public class HandCarFactory implements ICarFactory<EmptyEngineParams> {

    /**
     * Creates a car with a manual engine.
     *
     * @param carParams the parameters to pass to the engine
     * @param carNumber the number of the car
     * @return the created car
     */
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/HandCarFactory.java
    @Override
    public Car createCar(EmptyEngineParams carParams, int carNumber) {
        var engine = new HandEngine(); // Creates a manual engine with no parameters

        return new Car(carNumber, engine); // Creates a car with the manual engine
    }
}

