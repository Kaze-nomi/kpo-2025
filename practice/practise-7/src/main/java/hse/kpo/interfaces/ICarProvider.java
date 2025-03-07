package hse.kpo.interfaces;

import hse.kpo.domains.Car;
import hse.kpo.domains.Customer;
import hse.kpo.domains.Ship;
import hse.kpo.factories.ShipWithWheelsFactory;

public interface ICarProvider {
    Car takeCar(Customer customer); // Метод возвращает optional на Car, что означает, что метод может ничего не вернуть
    public <TParams> Car addCar(ICarFactory<TParams> carFactory, TParams carParams);
    public Car addShipWithWheels(ShipWithWheelsFactory shipWithWheelsFactory, Ship ship);
}
