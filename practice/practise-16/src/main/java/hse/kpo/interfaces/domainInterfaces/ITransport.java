package hse.kpo.interfaces.domainInterfaces;

import hse.kpo.domains.customers.Customer;

public interface ITransport {
    boolean isCompatible(Customer customer);
    int getID(); 
    String getEngineType();
    String getTransportType();
}