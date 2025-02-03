package hse.kpo.ServicesTests;

import hse.kpo.domains.Customer;
import hse.kpo.domains.Car;
import hse.kpo.services.CarService;
import hse.kpo.services.CustomerStorage;
import hse.kpo.services.HseCarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KpoServicesTest_2 {

	@Test
	@DisplayName("Тест продажи машин с использованием mock-объекта")
	void testSellCarsWithMock() {
		CarService carServiceMock = mock(CarService.class);
		when(carServiceMock.takeCar(any(Customer.class))).thenReturn(new Car());

		// Создаем реальный CustomerStorage
		CustomerStorage customerStorage = new CustomerStorage();
		customerStorage.addCustomer(new Customer("John", 6, 4, 50));
		customerStorage.addCustomer(new Customer("Bob", 4, 6, 200));

		// Создаем HseCarService с mock-объектом
		HseCarService hseCarService = new HseCarService(carServiceMock, customerStorage);

		// Вызываем метод и проверяем результат
		hseCarService.sellCars();
		assertNotNull(customerStorage.getCustomers().get(0).getCar());
		assertNotNull(customerStorage.getCustomers().get(1).getCar());
	}
}