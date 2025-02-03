package hse.kpo.services;

import hse.kpo.domains.Customer;
import hse.kpo.interfaces.ICustomerProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

    /**
     * List of customers.
     */

public class CustomerStorage implements ICustomerProvider {
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

