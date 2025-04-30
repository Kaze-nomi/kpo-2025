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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import hse.kpo.builders.Report;
import hse.kpo.domains.cars.Car;
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

    public void addTransportFromReport(ReportFormat format) {
        try {
            switch (format) {
                case CSV -> loadTransportFromCSV("reports/transport.csv");
                case XML -> loadTransportFromXML("reports/transport.xml");
                default -> throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load transport data", e);
        }
    }

    private void loadTransportFromCSV(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int vin = Integer.parseInt(values[0]);
                String type = values[1];
                String engineType = values[2];

                switch (type) {
                    case "Car":
                        switch (engineType) {
                            case "HandEngine()":
                                addHandCar();
                                break;
                            case "LevitationEngine()":
                                addLevitationCar();
                                break;
                            default:
                                if (engineType.startsWith("PedalEngine(size=")) {
                                    int size = Integer.parseInt(engineType.substring(engineType.indexOf('=') + 1, engineType.indexOf(')')));
                                    addPedalCar(size);
                                } else if (vin >= 10000) {
                                    addShipWithWheels();
                                } else {
                                    throw new IllegalArgumentException("Unsupported engine type: " + engineType);
                                }
                                break;
                        }
                        break;
                    case "Ship":
                        switch (engineType) {
                            case "HandEngine()":
                                addHandShip();
                                break;
                            case "LevitationEngine()":
                                addLevitationShip();
                                break;
                            default:
                                if (engineType.startsWith("PedalEngine")) {
                                    int size = Integer.parseInt(engineType.substring(engineType.indexOf('=') + 1, engineType.indexOf(')')));
                                    addPedalShip(size);
                                } else {
                                    throw new IllegalArgumentException("Unsupported engine type: " + engineType);
                                }
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported transport type: " + type);
                }
            }
        }
    }

    private void loadTransportFromXML(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<VIN>")) {
                    int vin = Integer.parseInt(line.trim().replaceAll("<[^>]+>", ""));
                    line = br.readLine(); // Type line
                    String type = line.trim().replaceAll("<[^>]+>", "");
                    line = br.readLine(); // Engine start
                    line = br.readLine(); // Engine type
                    String engineType = line.trim().replaceAll("<[^>]+>", "");

                    switch (type) {
                        case "Car":
                            if (vin >= 10000) {
                                addShipWithWheels();
                            } else if (engineType.equals("HandEngine()")) {
                                addHandCar();
                            } else if (engineType.equals("LevitationEngine()")) {
                                addLevitationCar();
                            } else if (engineType.startsWith("PedalEngine(size=")) {
                                int size = Integer.parseInt(engineType.replaceAll("[^0-9]", ""));
                                addPedalCar(size);
                            } else {
                                throw new IllegalArgumentException("Unsupported engine type: " + engineType);
                            }
                            break;
                        case "Ship":
                            if (engineType.equals("HandEngine()")) {
                                addHandShip();
                            } else if (engineType.equals("LevitationEngine()")) {
                                addLevitationShip();
                            } else if (engineType.startsWith("PedalEngine(size=")) {
                                int size = Integer.parseInt(engineType.replaceAll("[^0-9]", ""));
                                addPedalShip(size);
                            } else {
                                throw new IllegalArgumentException("Unsupported engine type: " + engineType);
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported transport type: " + type);
                    }
                }
            }
        }
    }

    public Customer addCustomer(String name, int legPower, int handPower, int iq) {
        return hseService.getCustomerProvider().addCustomer(new Customer(name, legPower, handPower, iq));
    }

    public Car addPedalCar(int pedalSize) {
        return hseService.getCarProvider().addCar(pedalCarFactory, new PedalEngineParams(pedalSize));
    }

    public Car addHandCar() {
        return hseService.getCarProvider().addCar(handCarFactory, EmptyEngineParams.DEFAULT);
    }

    public Car addLevitationCar() {
        return hseService.getCarProvider().addCar(levitationCarFactory, EmptyEngineParams.DEFAULT);
    }

    public Ship addPedalShip(int pedalSize) {
        return hseService.getShipProvider().addShip(pedalShipFactory, new PedalEngineParams(pedalSize));
    }

    public Ship addHandShip() {
        return hseService.getShipProvider().addShip(handShipFactory, new EmptyEngineParams());
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

    public void exportTransport(ReportFormat format, Writer writer) {
        List<ITransport> transport = Stream.concat(
                hseService.getCarProvider().getCars().stream(),
                hseService.getShipProvider().getShips().stream())
                .toList();
        ITransportExporter exporter = reportExporterFactory.createTransoport(format);
        try {
            exporter.export(transport, writer);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Report generateReport() {
        return reportSalesObserver.buildReport();
    }
}
