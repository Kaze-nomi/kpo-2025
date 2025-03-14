package hse.kpo.FactoriesTests;

import hse.kpo.domains.customers.Customer;
import hse.kpo.factories.carFactories.LevitationCarFactory;
import hse.kpo.services.ShipService;
import hse.kpo.params.EmptyEngineParams;
import hse.kpo.services.CarService;
import hse.kpo.services.CustomerStorage;
import hse.kpo.services.HseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KpoFactoriesTest_3 {

	@Autowired
	private LevitationCarFactory levitationCarFactory;

@Test
@DisplayName("Тест на проверку продажи машины покупателю с несоответствующими для машины параметрами")
void testSellCarToStupidCustomer() {
    // Создаем реальный CarService
    CarService carService = new CarService();

    // Создаем реальный ShipProvider
    ShipService shipProvider = new ShipService();

    // Создаем реальный CustomerStorage
    CustomerStorage customerStorage = new CustomerStorage();
    customerStorage.addCustomer(new Customer("John", 6, 4, 50));

    carService.addCar(levitationCarFactory, EmptyEngineParams.DEFAULT);


    // Создаем HseCarService
    HseService hseCarService = new HseService(carService, shipProvider, customerStorage);

    // Вызываем метод sellCars
    hseCarService.sellCars();

    // Проверяем, что машина у покупателя не появилась
    assertNull(customerStorage.getCustomers().get(0).getCar());
    }
}