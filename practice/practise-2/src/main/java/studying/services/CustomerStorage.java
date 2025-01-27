<<<<<<< HEAD:practice/practise-2/src/main/java/studying/services/CustomerStorage.java
package studying.services;

import studying.domains.Customer;
import studying.interfaces.ICustomerProvider;

import java.util.ArrayList;
import java.util.List;

public class CustomerStorage implements ICustomerProvider {
=======
/**
 * Class for storing customers.
 * Implements {@link ICustomerProvider} interface.
 *
 * 
 * 
 */
public class CustomerStorage implements ICustomerProvider{
    /**
     * List of customers.
     */
    @Getter
>>>>>>> fae1144 (merge):practice/practise-2/src/main/java/studying/CustomerStorage.java
    private List<Customer> customers = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getCustomers() {
        return customers;
    }

    /**
     * Adds a customer to the storage.
     *
     * @param customer customer to add
     */
    public void addCustomer(Customer customer)
    {
        customers.add(customer); // simply add customer to the list
    }
}

