<<<<<<< HEAD:practice/practise-2/src/main/java/studying/interfaces/ICarFactory.java
package studying.interfaces;

import studying.domains.Car;
=======
/**
 * Interface for factories that create cars.
 * 
 * @param <TParams> the type of parameters that are passed to the factory
 */
package studying;
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/ICarFactory.java

/**
 * Interface for factories that create cars.
 * 
 * @param <TParams> the type of parameters that are passed to the factory
 */
public interface ICarFactory<TParams> {
    /**
     * Creates a car.
     * 
     * @param carParams the parameters to pass to the engine
     * @param carNumber the number of the car
     * @return the created car
     */
    Car createCar(TParams carParams, int carNumber);
}

