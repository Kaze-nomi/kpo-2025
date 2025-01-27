package hse.kpo.interfaces;

import hse.kpo.domains.Car;
/**
 * Interface for factories that create cars.
 * 
 * @param <TParams> the type of parameters that are passed to the factory
 */

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
    /**
     * Creates a car.
     * 
     * @param carParams the parameters to pass to the engine
     * @param carNumber the number of the car
     * @return the created car
     */
    Car createCar(TParams carParams, int carNumber);
}


