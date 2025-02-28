package hse.kpo;

import hse.kpo.facade.HSE;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
public class KpoApplication {
	public static void main(String[] args) {
		var context = SpringApplication.run(KpoApplication.class, args);

		HSE hse = context.getBean(HSE.class);
		
		hse.addCustomer("Иван", 100, 100, 100);
		hse.addCustomer("Вася", 100, 100, 100);
		hse.addCustomer("Петя", 100, 100, 100);
		hse.addCustomer("Глеб", 100, 100, 100);

		hse.addPedalCar(10);
		hse.addHandCar();

		hse.addPedalShip(10);
		hse.addHandShip();

		hse.addShipWithWheels(10);

		hse.sellCars();
		hse.sellShips();

		System.out.println(hse.generateReport());
	}
}