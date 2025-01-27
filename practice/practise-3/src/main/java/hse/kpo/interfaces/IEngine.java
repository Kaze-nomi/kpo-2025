package hse.kpo.interfaces;


import hse.kpo.domains.Customer;

/**
 * Interface representing an engine.
 */
public interface IEngine {

    /**
     * Checks if the engine is compatible with the customer.
     *
     * @param customer the customer to check
     * @return true if the engine is compatible with the customer, false otherwise
     */
    boolean isCompatible(Customer customer);
}

