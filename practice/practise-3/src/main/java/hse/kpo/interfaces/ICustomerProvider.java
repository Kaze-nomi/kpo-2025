<<<<<<< HEAD:practice/practise-3/src/main/java/hse/kpo/interfaces/ICustomerProvider.java
package hse.kpo.interfaces;
=======
/**
 * Interface for providers that provide customers to other classes.
 * 
 */
package studying;
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/ICustomerProvider.java

import hse.kpo.domains.Customer;
import java.util.List;

/**
 * Interface for providers that provide customers to other classes.
 * 
 */
public interface ICustomerProvider {
    /**
     * Returns a read-only collection of customers.
     * 
     * @return a read-only collection of customers
     */
    List<Customer> getCustomers();
}

