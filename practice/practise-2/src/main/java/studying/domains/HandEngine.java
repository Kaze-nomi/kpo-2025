<<<<<<< HEAD:practice/practise-2/src/main/java/studying/domains/HandEngine.java
package studying.domains;

import lombok.ToString;
import studying.interfaces.IEngine;

@ToString
public class HandEngine implements IEngine {
=======
/**
 * A hand-powered engine.
 */
@ToString
public class HandEngine implements IEngine {
    /**
     * Checks if the engine is compatible with the customer.
     * @param customer the customer to check
     * @return true if the engine is compatible with the customer, false otherwise
     */
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/HandEngine.java
    @Override
    public boolean isCompatible(Customer customer) {
        return customer.getHandPower() > 5;
    }
}

