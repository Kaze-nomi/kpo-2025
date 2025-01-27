/**
 * Represents a pedal engine.
 * 
 */
package studying;

import lombok.Getter;
import lombok.ToString;
import studying.interfaces.IEngine;

/**
 * Represents a pedal engine.
 * 
 */
@ToString
@Getter
public class PedalEngine implements IEngine {
    private final int size;

    /**
     * Checks if the engine is compatible with the customer.
     * 
     * @param customer the customer to check
     * @return true if the customer's leg power is 5 or higher, false otherwise
     */
    @Override
    public boolean isCompatible(Customer customer) {
        return customer.getLegPower() > 5;
    }

    /**
     * Creates a pedal engine with the specified size.
     * 
     * @param size the size of the pedal engine
     */
    public PedalEngine(int size) {
        this.size = size;
    }
}

