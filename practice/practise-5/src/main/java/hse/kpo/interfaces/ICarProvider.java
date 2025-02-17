package hse.kpo.interfaces;

import hse.kpo.domains.Car;
import hse.kpo.domains.Customer;

public interface ICarProvider {
    Car takeCar(Customer customer); // Метод возвращает optional на Car, что означает, что метод может ничего не вернуть
    public <TParams> void addCar(ICarFactory<TParams> carFactory, TParams carParams);
}
