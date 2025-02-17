package hse.kpo.interfaces;

import hse.kpo.domains.Ship;
import hse.kpo.domains.Customer;

public interface IShipProvider {
    Ship takeShip(Customer customer); // Метод возвращает optional на Ship, что означает, что метод может ничего не вернуть
    public <TParams> void addShip(IShipFactory<TParams> shipFactory, TParams shipParams);
}
