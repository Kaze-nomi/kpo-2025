package hse.kpo.services;

import hse.kpo.domains.cars.Car;
import hse.kpo.domains.customers.Customer;
import hse.kpo.domains.ships.Ship;
import hse.kpo.factories.carFactories.ShipWithWheelsFactory;
import hse.kpo.interfaces.factoryInterfaces.ICarFactory;
import hse.kpo.interfaces.providerInterfaces.ICarProvider;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CarService implements ICarProvider {

    @Getter
    private final List<Car> cars = new ArrayList<>();

    private int carNumberCounter = 0;

    @Override
    public Car takeCar(Customer customer) {
        

        var filteredCars = cars.stream().filter(car -> car.isCompatible(customer)).toList();

        var firstCar = filteredCars.stream().findFirst();

        firstCar.ifPresent(cars::remove);

        return firstCar.orElse(null);
    }

    @Override
    public <TParams> Car addCar(ICarFactory<TParams> carFactory, TParams carParams)
    {
        // создаем автомобиль из переданной фабрики
        var car = carFactory.createCar(
                carParams, // передаем параметры
                ++carNumberCounter // передаем номер - номер будет начинаться с 1
        );

        if (car == null) {
            log.error("Failed to create car");
            return car;
        }

        cars.add(car); // добавляем автомобиль
        return car;
    }

    public Car addShipWithWheels(ShipWithWheelsFactory shipWithWheelsFactory, Ship ship) {

        var shipWithWheels = shipWithWheelsFactory.createShipWithWheels(ship);
        if (shipWithWheels == null) {
            log.error("Failed to create car");
            return shipWithWheels;
        }
        cars.add(shipWithWheels);
        return shipWithWheels;
    }
}
