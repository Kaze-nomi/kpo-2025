package hse.kpo.services;

import hse.kpo.domains.Customer;
import hse.kpo.interfaces.ICustomerProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
<<<<<<< HEAD
public class CustomerStorage implements ICustomerProvider {
<<<<<<< HEAD
    private final List<Customer> customers = new ArrayList<>();
=======
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
=======
@RequiredArgsConstructor
public class CustomerStorage implements ICustomerProvider {
>>>>>>> dfd8807 (fix)
    private List<Customer> customers = new ArrayList<>();
>>>>>>> 2684c20 (delete)

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

