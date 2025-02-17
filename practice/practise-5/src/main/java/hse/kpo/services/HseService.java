package hse.kpo.services;

import hse.kpo.interfaces.ICarProvider;
import hse.kpo.interfaces.ICustomerProvider;
import hse.kpo.interfaces.IShipProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class HseService {

    @Getter
    private final ICarProvider carProvider;

    @Getter
    private final IShipProvider shipService;

    @Getter
    private final ICustomerProvider customerProvider;

    public void sellCars() {
        // получаем список покупателей
        var customers = customerProvider.getCustomers();
        // пробегаемся по полученному списку
        customers.stream().filter(customer -> Objects.isNull(customer.getCar()))
                .forEach(customer -> {
                    var car = carProvider.takeCar(customer);
                    if (Objects.nonNull(car)) {
                        customer.setCar(car);
                    }
                    else {
                        log.warn("Car not found for customer {}", customer.getName());
                    }
                });
    }

    public void sellShips() {
        // получаем список покупателей
        var customers = customerProvider.getCustomers();
        // пробегаемся по полученному списку
        customers.stream().filter(customer -> Objects.isNull(customer.getCar()))
                .forEach(customer -> {
                    var ship = shipService.takeShip(customer);
                    if (Objects.nonNull(ship)) {
                        customer.setShip(ship);
                    } 
                    else {
                        log.warn("Ship not found for customer {}", customer.getName());
                    }
                });
    }

}