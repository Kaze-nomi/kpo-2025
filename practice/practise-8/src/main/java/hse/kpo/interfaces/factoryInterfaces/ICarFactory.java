package hse.kpo.interfaces.factoryInterfaces;

import hse.kpo.domains.cars.Car;

public interface ICarFactory<TParams> {
    Car createCar(TParams carParams, int carNumber);
}