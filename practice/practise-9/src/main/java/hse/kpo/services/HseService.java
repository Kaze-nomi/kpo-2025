package hse.kpo.services;

import hse.kpo.domains.customers.Customer;
import hse.kpo.params.ProductionTypes;

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

    @Sales
    public void sellCars() {
        // получаем список покупателей
        var customers = customerProvider.getCustomers();
        // пробегаемся по полученному списку
        customers.stream().filter(customer -> Objects.isNull(customer.getCar()))
                .forEach(customer -> {
                    var car = carProvider.takeCar(customer);
                    if (Objects.nonNull(car)) {
                        customer.setCar(car);
                        notifyObserversForSale(customer, ProductionTypes.CAR, car.getVIN());
                    }
                    else {
                        log.warn("Car not found for customer {}", customer.getName());
                    }
                });
    }


    @Sales
    public void sellCar(int vin) {
        // получаем список покупателей
        var customers = customerProvider.getCustomers();
        // пробегаемся по полученному списку
        customers.stream().filter(customer -> Objects.isNull(customer.getCar()))
                .filter(customer -> carProvider.getCars().stream()
                        .anyMatch(car -> car.getID() == vin))
                .forEach(customer -> {
                    var car = carProvider.takeCar(customer);
                    if (Objects.nonNull(car)) {
                        customer.setCar(car);
                        notifyObserversForSale(customer, ProductionTypes.CAR, vin);
                    }
                    else {
                        log.warn("Car with vin {} not found for customer {}", vin, customer.getName());
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