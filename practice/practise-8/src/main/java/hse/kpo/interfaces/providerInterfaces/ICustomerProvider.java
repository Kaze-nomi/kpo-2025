package hse.kpo.interfaces.providerInterfaces;

import java.util.List;

import hse.kpo.domains.customers.Customer;

public interface ICustomerProvider {
    List<Customer> getCustomers(); // метод возвращает коллекцию только для чтения, так как мы не хотим давать вызывающему коду возможность изменять список
    public void addCustomer(Customer customer);
}
