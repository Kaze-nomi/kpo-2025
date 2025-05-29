package hse.kpo.interfaces.providerInterfaces;

import java.util.List;
import java.util.Optional;

import hse.kpo.domains.customers.Customer;
import hse.kpo.dto.CustomerRequest;

public interface ICustomerProvider {
    List<Customer> getCustomers(); // метод возвращает коллекцию только для чтения, так как мы не хотим давать вызывающему коду возможность изменять список
    public Customer addCustomer(Customer customer);
    public Customer updateCustomer(CustomerRequest request);
    public boolean deleteCustomer(String name);
    public Optional<Customer> findById(int id);

}
