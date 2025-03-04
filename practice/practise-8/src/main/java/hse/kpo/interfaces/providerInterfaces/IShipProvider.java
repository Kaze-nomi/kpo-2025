package hse.kpo.interfaces.providerInterfaces;

import java.util.List;

import hse.kpo.domains.customers.Customer;
import hse.kpo.domains.ships.Ship;
import hse.kpo.interfaces.factoryInterfaces.IShipFactory;

public interface IShipProvider {
    Ship takeShip(Customer customer); // Метод возвращает optional на Ship, что означает, что метод может ничего не вернуть
    public <TParams> Ship addShip(IShipFactory<TParams> shipFactory, TParams shipParams);
    public void deleteShip(int VIN);
    public List<Ship> getShips();
}
