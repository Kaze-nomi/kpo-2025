package hse.kpo.facade;

import hse.kpo.services.*;
import jakarta.annotation.PostConstruct;
import hse.kpo.factories.*;
import hse.kpo.observers.*;
import hse.kpo.params.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import hse.kpo.builders.Report;
import hse.kpo.domains.Customer;

@Component
@RequiredArgsConstructor
public class HSE {

    private final PedalCarFactory pedalCarFactory;

    private final HandCarFactory handCarFactory;

    private final PedalShipFactory pedalShipFactory;

    private final HandShipFactory handShipFactory;

    private final ReportSalesObserver reportSalesObserver;

    private final LevitationShipFactory levitationShipFactory;

    private final LevitationCarFactory levitationCarFactory;

    private final ShipWithWheelsFactory shipWithWheelsFactory;

    private final HseService hseService;

    @PostConstruct
    public void init() {
        hseService.addObserver(reportSalesObserver);
    }

    public void addCustomer(String name, int legPower, int handPower, int iq) {
        hseService.getCustomerProvider().addCustomer(new Customer(name, legPower, handPower, iq));
    }

    public void addPedalCar(int pedalSize) {
        hseService.getCarProvider().addCar(pedalCarFactory, new PedalEngineParams(pedalSize));
    }

    public void addHandCar() {
        hseService.getCarProvider().addCar(handCarFactory, new EmptyEngineParams());
    }

    public void addLevitationCar() {
        hseService.getCarProvider().addCar(levitationCarFactory, new EmptyEngineParams());
    }

    public void addPedalShip(int pedalSize) {
        hseService.getShipProvider().addShip(pedalShipFactory, new PedalEngineParams(pedalSize));
    }

    public void addHandShip() {
        hseService.getShipProvider().addShip(handShipFactory, new EmptyEngineParams());
    }

    public void addLevitationShip() {
        hseService.getShipProvider().addShip(levitationShipFactory, new EmptyEngineParams());
    }

    public void addShipWithWheels(int pedalSize) {
        hseService.getCarProvider().addCar(shipWithWheelsFactory, new PedalEngineParams(pedalSize));
    }

    public void sellCars() {
        hseService.sellCars();
    }

    public void sellShips() {
        hseService.sellShips();
    }

    public Report generateReport() {
        return reportSalesObserver.buildReport();
    }
}
