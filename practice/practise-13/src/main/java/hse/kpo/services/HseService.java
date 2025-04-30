package hse.kpo.services;

import hse.kpo.domains.cars.Car;
import hse.kpo.domains.customers.Customer;
import hse.kpo.params.ProductionTypes;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import hse.kpo.observers.Sales;
import hse.kpo.interfaces.observerInterfaces.ISalesObserver;
import hse.kpo.interfaces.providerInterfaces.ICarProvider;
import hse.kpo.interfaces.providerInterfaces.ICustomerProvider;
import hse.kpo.interfaces.providerInterfaces.IShipProvider;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class HseService {

    final List<ISalesObserver> observers = new ArrayList<>();

    @Getter
    private final ICarProvider carProvider;

    @Getter
    private final IShipProvider shipProvider;

    @Getter
    private final ICustomerProvider customerProvider;

    @Transactional
    @Sales
    public void sellCars() {
        customerProvider.getCustomers().stream()
            .filter(customer -> customer.getCars() == null || customer.getCars().isEmpty())
            .forEach(customer -> {
                Car car = carProvider.takeCar(customer);
                if (Objects.nonNull(car)) {
                    customer.getCars().add(car); // Добавляем автомобиль в список клиента
                    car.setCustomer(customer);   // Устанавливаем ссылку на клиента в автомобиле
                    notifyObserversForSale(customer, ProductionTypes.CAR, car.getID());
                } else {
                    log.warn("No car in CarService");
                }
            });
    }


    @Sales
    public void sellCar(int vin) {
        // получаем список покупателей
        var customers = customerProvider.getCustomers();
        // пробегаемся по полученному списку
        customers.stream()
                .filter(customer -> customer.getCars() == null || customer.getCars().isEmpty())
                .forEach(customer -> {
                    var carOptional = carProvider.getCars().stream()
                            .filter(car -> car.getID() == vin)
                            .findFirst();
                    if (carOptional.isPresent()) {
                        var car = carOptional.get();
                        carProvider.getCars().remove(car);
                        customer.getCars().add(car);
                        car.setCustomer(customer);
                        carProvider.addExistingCar(car);
                        notifyObserversForSale(customer, ProductionTypes.CAR, vin);
                    } else {
                        log.warn("Car with vin {} not found", vin);
                    }
                });
    }

    @Sales
    public void sellShips() {
        // получаем список покупателей
        var customers = customerProvider.getCustomers();
        // пробегаемся по полученному списку
        customers.stream().filter(customer -> Objects.isNull(customer.getShip()))
                .forEach(customer -> {
                    var ship = shipProvider.takeShip(customer);
                    if (Objects.nonNull(ship)) {
                        customer.setShip(ship);
                        notifyObserversForSale(customer, ProductionTypes.CATAMARAN, ship.getVIN());
                    } 
                    else {
                        log.warn("Ship not found for customer {}", customer.getName());
                    }
                });
    }

    @Sales
    public void sellShip(int vin) {
        // получаем список покупателей
        var customers = customerProvider.getCustomers();
        // пробегаемся по полученному списку
        customers.stream().filter(customer -> Objects.isNull(customer.getShip()))
                .filter(customer -> shipProvider.getShips().stream()
                        .anyMatch(ship -> ship.getID() == vin))
                .forEach(customer -> {
                    var ship = shipProvider.takeShip(customer);
                    if (Objects.nonNull(ship)) {
                        customer.setShip(ship);
                        notifyObserversForSale(customer, ProductionTypes.CATAMARAN, vin);
                    } 
                    else {
                        log.warn("Ship with vin {} not found for customer {}", vin, customer.getName());
                    }
                });
    }

    public void addObserver(ISalesObserver observer) {
        observers.add(observer);
    }

    private void notifyObserversForSale(Customer customer, ProductionTypes productType, int vin) {
        observers.forEach(obs -> obs.onSale(customer, productType, vin));
    }

}