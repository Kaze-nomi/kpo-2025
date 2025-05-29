package hse.kpo.services;

import hse.kpo.domains.cars.Car;
import hse.kpo.domains.customers.Customer;
import hse.kpo.domains.ships.Ship;
import hse.kpo.factories.carFactories.ShipWithWheelsFactory;
import hse.kpo.interfaces.factoryInterfaces.ICarFactory;
import hse.kpo.interfaces.providerInterfaces.ICarProvider;
import hse.kpo.repositories.CarRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarService implements ICarProvider {

    private final CarRepository carRepository;

    @Override
    public Car takeCar(Customer customer) {
        return carRepository.findAll().stream()
                .filter(car -> car.isCompatible(customer))
                .findFirst()
                .orElse(null);
    }

    /**
     * Метод добавления {@link Car} в систему.
     *
     * @param carFactory фабрика для создания автомобилей
     * @param carParams  параметры для создания автомобиля
     */
    public <T> Car addCar(ICarFactory<T> carFactory, T carParams) {
        return carRepository.save(carFactory.createCar(carParams));
    }

    public Car addExistingCar(Car car) {
        return carRepository.save(car);
    }

    @Transactional
    @Cacheable(value = "cars", key = "#vin")
    public Optional<Car> findByVin(Integer vin) throws InterruptedException {
        Thread.sleep(2000);
        return carRepository.findById(vin);
    }

    @CacheEvict(value = "cars", key = "#vin")
    public boolean deleteCar(Integer vin) {
        try {
            carRepository.deleteById(vin);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Car addShipWithWheels(ShipWithWheelsFactory shipWithWheelsFactory, Ship ship) {

        var shipWithWheels = shipWithWheelsFactory.createShipWithWheels(ship);
        if (shipWithWheels == null) {
            log.error("Failed to create car");
            return shipWithWheels;
        }
        return carRepository.save(shipWithWheels);
    }

    public List<Car> getCars() {
        return carRepository.findAll();
    }
}
