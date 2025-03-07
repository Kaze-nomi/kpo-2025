package hse.kpo.interfaces.engineInterfaces;


import hse.kpo.domains.customers.Customer;
import hse.kpo.params.ProductionTypes;

public interface IEngine {

    /**
     * Метод для проверки совместимости двигателя с покупателем.
     *
     * @param customer - покупатель, с которым мы сравниваем двигатель
     * @return true, если двигатель подходит покупателю
     */
    boolean isCompatible(Customer customer, ProductionTypes productionType);
}
