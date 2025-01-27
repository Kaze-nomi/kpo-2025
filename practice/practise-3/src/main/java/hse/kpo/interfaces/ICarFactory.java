package hse.kpo.interfaces;

import hse.kpo.domains.Car;
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> fa73bc1 (delete)
=======
>>>>>>> a04facd (fix)
/**
 * Interface for factories that create cars.
 * 
 * @param <TParams> the type of parameters that are passed to the factory
 */
<<<<<<< HEAD
<<<<<<< HEAD
=======
package studying;
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/ICarFactory.java
>>>>>>> fa73bc1 (delete)
=======
>>>>>>> a04facd (fix)

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

