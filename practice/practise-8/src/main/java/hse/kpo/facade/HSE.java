package hse.kpo.facade;

import hse.kpo.services.*;
import jakarta.annotation.PostConstruct;
import hse.kpo.factories.carFactories.HandCarFactory;
import hse.kpo.factories.carFactories.LevitationCarFactory;
import hse.kpo.factories.carFactories.PedalCarFactory;
import hse.kpo.factories.carFactories.ShipWithWheelsFactory;
import hse.kpo.factories.exportFactories.ReportExporterFactory;
import hse.kpo.factories.shipFactories.HandShipFactory;
import hse.kpo.factories.shipFactories.LevitationShipFactory;
import hse.kpo.factories.shipFactories.PedalShipFactory;
import hse.kpo.observers.*;
import hse.kpo.params.*;

import lombok.RequiredArgsConstructor;

import java.io.Writer;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import hse.kpo.builders.Report;
import hse.kpo.domains.customers.Customer;
import hse.kpo.domains.ships.Ship;
import hse.kpo.interfaces.domainInterfaces.ITransport;
import hse.kpo.interfaces.exporterInterfaces.IReportExporter;
import hse.kpo.interfaces.exporterInterfaces.ITransportExporter;

@Component
@RequiredArgsConstructor
public class HSE {

    private final PedalCarFactory pedalCarFactory;

    private final HandCarFactory handCarFactory;

    private final PedalShipFactory pedalShipFactory;

    private final HandShipFactory handShipFactory;

    private final ReportSalesObserver reportSalesObserver;

    private final ReportExporterFactory reportExporterFactory;

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

    public Ship addLevitationShip() {
        return hseService.getShipProvider().addShip(levitationShipFactory, new EmptyEngineParams());
    }

    public void addShipWithWheels() {
        Ship ship = addLevitationShip();
        hseService.getShipProvider().deleteShip(ship.getVIN());
        hseService.getCarProvider().addShipWithWheels(shipWithWheelsFactory, ship);

    }

    public void sellCars() {
        hseService.sellCars();
    }

    public void sellShips() {
        hseService.sellShips();
    }

    public void exportReport(ReportFormat format, Writer writer) {
        Report report = reportSalesObserver.buildReport();
        IReportExporter exporter = reportExporterFactory.createReport(format);

        try {
            exporter.export(report, writer);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void exportTransports(ReportFormat format, Writer writer) {
        List<ITransport> transports = Stream.concat(
                hseService.getCarProvider().getCars().stream(),
                hseService.getShipProvider().getShips().stream())
                .toList();
        ITransportExporter exporter = reportExporterFactory.createTransoport(format);
        try {
            exporter.export(transports, writer);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Report generateReport() {
        return reportSalesObserver.buildReport();
    }
}
