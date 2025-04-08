package hse.kpo.services;

import hse.kpo.domains.customers.Customer;
import hse.kpo.interfaces.providerInterfaces.ICustomerProvider;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerStorage implements ICustomerProvider {
    private final List<Customer> customers = new ArrayList<>();

    @Override
    public List<Customer> getCustomers() {
        return customers;
    }

    @Override
    public Customer addCustomer(Customer customer)
    {
        customers.add(customer); // просто добавляем покупателя в список
        return customer;
    }
}
