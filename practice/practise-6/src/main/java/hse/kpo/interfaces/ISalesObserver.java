package hse.kpo.interfaces;

import hse.kpo.domains.Customer;
import hse.kpo.params.ProductionTypes;

public interface ISalesObserver {
    void onSale(Customer customer, ProductionTypes productType, int vin);
    void checkCustomers();
}