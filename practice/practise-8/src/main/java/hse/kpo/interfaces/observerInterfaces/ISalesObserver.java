package hse.kpo.interfaces.observerInterfaces;

import hse.kpo.domains.customers.Customer;
import hse.kpo.params.ProductionTypes;

public interface ISalesObserver {
    void onSale(Customer customer, ProductionTypes productType, int vin);
    void checkCustomers();
}