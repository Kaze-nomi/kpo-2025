<<<<<<< HEAD:practice/practise-2/src/main/java/studying/interfaces/ICarProvider.java
package studying.interfaces;

import studying.domains.Car;
import studying.domains.Customer;
=======
/**
 * Interface for providers that provide cars to customers.
 * 
 */
package studying;
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/ICarProvider.java

/**
 * Interface for providers that provide cars to customers.
 * 
 */
public interface ICarProvider {

    /**
     * Takes a car from the list that is compatible with the customer.
     * 
     * @param customer the customer to check
     * @return the car if it is compatible, null otherwise
     */
    Car takeCar(Customer customer); // Метод возвращает optional на Car, что означает, что метод может ничего не вернуть
}