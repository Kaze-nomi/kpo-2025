<<<<<<< HEAD
<<<<<<< HEAD:practice/practise-3/src/main/java/hse/kpo/domains/PedalEngine.java
package hse.kpo.domains;
=======
/**
 * Represents a pedal engine.
 * 
 */
package studying;
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/PedalEngine.java
=======
package hse.kpo.domains;
>>>>>>> 2894323 (really good fixes)

import hse.kpo.interfaces.IEngine;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a pedal engine.
<<<<<<< HEAD
=======
 * 
>>>>>>> fa73bc1 (delete)
 */
@ToString
@Getter
public class PedalEngine implements IEngine {
<<<<<<< HEAD

    /**
     * The size of the pedal engine.
<<<<<<< HEAD
     */
=======
<<<<<<< HEAD:practice/practise-3/src/main/java/hse/kpo/domains/PedalEngine.java
=======
    /**
     * The size of the pedal engine.
     */
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/PedalEngine.java
>>>>>>> fa73bc1 (delete)
=======
    */
    
>>>>>>> 2894323 (really good fixes)
    private final int size;

    /**
     * Checks if the engine is compatible with the customer.
<<<<<<< HEAD
     *
=======
     * 
>>>>>>> fa73bc1 (delete)
     * @param customer the customer to check
     * @return true if the customer's leg power is 5 or higher, false otherwise
     */
    @Override
    public boolean isCompatible(Customer customer) {
        return customer.getLegPower() > 5;
    }

    /**
     * Creates a pedal engine with the specified size.
<<<<<<< HEAD
     *
=======
     * 
>>>>>>> fa73bc1 (delete)
     * @param size the size of the pedal engine
     */
    public PedalEngine(int size) {
        this.size = size;
    }
}

