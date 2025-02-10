package hse.kpo.FactoriesTests;

import hse.kpo.domains.Customer;
import hse.kpo.factories.LevitationCarFactory;
import hse.kpo.params.EmptyEngineParams;
import hse.kpo.services.CarService;
import hse.kpo.services.CustomerStorage;
import hse.kpo.services.HseCarService;
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

    // Создаем реальный CustomerStorage
    CustomerStorage customerStorage = new CustomerStorage();
    customerStorage.addCustomer(new Customer("John", 6, 4, 50));

    carService.addCar(levitationCarFactory, EmptyEngineParams.DEFAULT);

    // Создаем HseCarService
    HseCarService hseCarService = new HseCarService(carService, customerStorage);

    // Вызываем метод sellCars
    hseCarService.sellCars();

    // Проверяем, что машина у покупателя не появилась
    assertNull(customerStorage.getCustomers().get(0).getCar());
    }
}