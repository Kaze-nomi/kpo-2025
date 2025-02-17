package hse.kpo;

import hse.kpo.services.HseService;
import lombok.extern.slf4j.Slf4j;
import hse.kpo.domains.ReportBuilder;
import hse.kpo.factories.PedalCarFactory;
import hse.kpo.params.PedalEngineParams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
public class KpoApplication {
	public static void main(String[] args) {
		var context = SpringApplication.run(KpoApplication.class, args);

		PedalCarFactory pedalCarFactory = context.getBean(PedalCarFactory.class);
		
		var hseService = context.getBean(HseService.class);

		hseService.getCustomerProvider().addCustomer(new hse.kpo.domains.Customer("Иван", 100, 100, 100));

		hseService.getCarProvider().addCar(pedalCarFactory, new PedalEngineParams(-5));

		var reportBuilder = new ReportBuilder()
				.addOperation("Инициализация системы")
				.addCustomers(hseService.getCustomerProvider().getCustomers());

		hseService.sellCars();

		var report = reportBuilder
				.addOperation("Продажа автомобилей")
				.addCustomers(hseService.getCustomerProvider().getCustomers())
				.build();

		System.out.println(report.toString());
	}
}
