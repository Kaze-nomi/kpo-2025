package hse.kpo.interfaces.factoryInterfaces;

import hse.kpo.domains.cars.Car;

public interface ICarFactory<T> {
    Car createCar(T parameters);
}