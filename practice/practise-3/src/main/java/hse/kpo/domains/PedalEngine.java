<<<<<<< HEAD:practice/practise-3/src/main/java/hse/kpo/domains/PedalEngine.java
package hse.kpo.domains;
=======
package hse.kpo.domains;
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/PedalEngine.java

import hse.kpo.interfaces.IEngine;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a pedal engine.
 * 
 */
@ToString
@Getter
public class PedalEngine implements IEngine {
<<<<<<< HEAD:practice/practise-3/src/main/java/hse/kpo/domains/PedalEngine.java
=======
    /**
     * The size of the pedal engine.
    */
    
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


