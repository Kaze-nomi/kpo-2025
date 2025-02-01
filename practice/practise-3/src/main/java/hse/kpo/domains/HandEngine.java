<<<<<<< HEAD:practice/practise-3/src/main/java/hse/kpo/domains/HandEngine.java
package hse.kpo.domains;

import hse.kpo.interfaces.IEngine;
import lombok.ToString;

/**
 * A hand-powered engine.
 */
@ToString
public class HandEngine implements IEngine {
<<<<<<< HEAD
<<<<<<< HEAD

    /**
     * Checks if the engine is compatible with the customer.
     *
     * @param customer the customer to check
     * @return true if the engine is compatible with the customer, false otherwise
     */
=======
=======
/**
 * A hand-powered engine.
 */
@ToString
public class HandEngine implements IEngine {
=======

>>>>>>> 72b345e (MeowHomework)
    /**
     * Checks if the engine is compatible with the customer.
     *
     * @param customer the customer to check
     * @return true if the engine is compatible with the customer, false otherwise
     */
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/HandEngine.java
>>>>>>> fa73bc1 (delete)
    @Override
    public boolean isCompatible(Customer customer) {
        return customer.getHandPower() > 5;
    }
}

