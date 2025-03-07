package hse.kpo;

import hse.kpo.facade.HSE;
import hse.kpo.params.ReportFormat;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class KpoApplication {
	public static void main(String[] args) {
		var context = SpringApplication.run(KpoApplication.class, args);

		HSE hse = context.getBean(HSE.class);

		hse.addCustomer("Иван", 100, 100, 555);
		hse.addCustomer("Вася", 100, 100, 555);
		hse.addCustomer("Петя", 100, 100, 555);
		hse.addCustomer("Глеб", 100, 100, 555);

		hse.addPedalCar(10);
		hse.addHandCar();

		hse.addPedalShip(10);
		hse.addHandShip();

		hse.addShipWithWheels();

		hse.sellCars();
		hse.sellShips();

		hse.addHandCar();
		hse.addHandShip();
		hse.addPedalCar(10);
		hse.addShipWithWheels();

		try (FileWriter fileWriter = new FileWriter("./reports/transport.xml")) {
			hse.exportTransport(ReportFormat.XML, fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (FileWriter fileWriter = new FileWriter("./reports/transport.csv")) {
			hse.exportTransport(ReportFormat.CSV, fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		hse.addTransportFromReport(ReportFormat.XML);

		hse.sellCars();
		hse.sellShips();

		try (FileWriter fileWriter = new FileWriter("./reports/report.MD")) {
			hse.exportReport(ReportFormat.MARKDOWN, fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (FileWriter fileWriter = new FileWriter("./reports/report.json")) {
			hse.exportReport(ReportFormat.JSON, fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}