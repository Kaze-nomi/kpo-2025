package hse.kpo;

import hse.kpo.services.HseService;
import lombok.extern.slf4j.Slf4j;
import hse.kpo.factories.HandCarFactory;
import hse.kpo.factories.PedalCarFactory;
import hse.kpo.factories.HandShipFactory;
import hse.kpo.factories.PedalShipFactory;
import hse.kpo.observers.ReportSalesObserver;
import hse.kpo.params.PedalEngineParams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
public class KpoApplication {
	public static void main(String[] args) {
		var context = SpringApplication.run(KpoApplication.class, args);

		PedalCarFactory pedalCarFactory = context.getBean(PedalCarFactory.class);

		HandCarFactory handCarFactory = context.getBean(HandCarFactory.class);

		PedalShipFactory pedalShipFactory = context.getBean(PedalShipFactory.class);

		HandShipFactory handShipFactory = context.getBean(HandShipFactory.class);

		ReportSalesObserver observer = context.getBean(ReportSalesObserver.class);

		var hseService = context.getBean(HseService.class);

		hseService.addObserver(observer);
		
		hseService.getCustomerProvider().addCustomer(new hse.kpo.domains.Customer("Иван", 100, 100, 100));

		hseService.getCustomerProvider().addCustomer(new hse.kpo.domains.Customer("Вася", 100, 100, 100));

		hseService.getCustomerProvider().addCustomer(new hse.kpo.domains.Customer("Петя", 100, 100, 100));

		hseService.getCarProvider().addCar(pedalCarFactory, new PedalEngineParams(10));

		hseService.getCarProvider().addCar(handCarFactory, null);

		hseService.getShipProvider().addShip(pedalShipFactory, new PedalEngineParams(10));

		hseService.getShipProvider().addShip(handShipFactory, null);

		hseService.sellCars();

		hseService.sellShips();

		System.out.println(observer.buildReport());
	}
}
