package hse.kpo.domains;

import hse.kpo.interfaces.IEngine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * <p>
 *     This class represents a car.
 * </p>
 */
/**
 * <p>
 *     This class represents a car.
 * </p>
 */
@ToString
@RequiredArgsConstructor
public class Car {

    @Getter
    private IEngine engine;

    /**
     * Get the VIN (Vehicle Identification Number) of the car.
     * @return the VIN of the car
     */
    /**
     * Get the VIN (Vehicle Identification Number) of the car.
     * @return the VIN of the car
     */
    @Getter
    private int VIN;

    /**
     * Creates a new car with specified VIN and engine.
     * @param VIN the VIN of the car
     * @param engine the engine of the car
     */
    public Car(int VIN, IEngine engine) {
        this.VIN = VIN;
>>>>>>> fa73bc1 (delete)
=======
     * @param vin the VIN of the car
     * @param engine the engine of the car
     */
    public Car(int vin, IEngine engine) {
        this.vin = vin;
>>>>>>> 72b345e (MeowHomework)
        this.engine = engine;
    }

    /**
     * Checks if the car is compatible with the customer.
     * @param customer the customer to check
     * @return true if the car is compatible with the customer, false otherwise
     */
    /**
     * Checks if the car is compatible with the customer.
     * @param customer the customer to check
     * @return true if the car is compatible with the customer, false otherwise
     */
    public boolean isCompatible(Customer customer) {
        return this.engine.isCompatible(customer); // inside the method just call the corresponding method of the engine
    }
}

