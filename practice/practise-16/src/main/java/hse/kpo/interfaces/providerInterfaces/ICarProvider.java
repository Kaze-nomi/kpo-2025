package hse.kpo.interfaces.providerInterfaces;

import java.util.List;

import hse.kpo.domains.cars.Car;
import hse.kpo.domains.customers.Customer;
import hse.kpo.domains.ships.Ship;
import hse.kpo.factories.carFactories.ShipWithWheelsFactory;
import hse.kpo.interfaces.factoryInterfaces.ICarFactory;

public interface ICarProvider {
    Car takeCar(Customer customer); // Метод возвращает optional на Car, что означает, что метод может ничего не вернуть
    public <TParams> Car addCar(ICarFactory<TParams> carFactory, TParams carParams);
    public Car addShipWithWheels(ShipWithWheelsFactory shipWithWheelsFactory, Ship ship);
    public Car addExistingCar(Car car);
    public List<Car> getCars();
}
