package hse.kpo;

import hse.kpo.domains.Customer;
import hse.kpo.domains.Car;
import hse.kpo.domains.HandEngine;
import hse.kpo.domains.LevitationEngine;
import hse.kpo.domains.PedalEngine;
import hse.kpo.factories.HandCarFactory;
import hse.kpo.factories.PedalCarFactory;
import hse.kpo.factories.LevitationCarFactory;
import hse.kpo.params.EmptyEngineParams;
import hse.kpo.params.PedalEngineParams;
import hse.kpo.services.CarService;
import hse.kpo.services.CustomerStorage;
import hse.kpo.services.HseCarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class KpoApplicationTests {

	@Autowired
	private CustomerStorage customerStorage;

	@Autowired
	private PedalCarFactory pedalCarFactory;

	@Autowired
	private HandCarFactory handCarFactory;

	@Autowired
	private LevitationCarFactory levitationCarFactory;

	@Test
	@DisplayName("Проверка каждой фабрики на соответствие типу производимого двигателя")
	void factoryCheck() {
		Car pedalCar = pedalCarFactory.createCar(new PedalEngineParams(1), 1);
		Car handCar = handCarFactory.createCar(EmptyEngineParams.DEFAULT, 2);
		Car levitationCar = levitationCarFactory.createCar(EmptyEngineParams.DEFAULT, 3);

		assertTrue(pedalCar.getEngine() instanceof PedalEngine);

		assertTrue(handCar.getEngine() instanceof HandEngine);

		assertTrue(levitationCar.getEngine() instanceof LevitationEngine);
	}

	@Test
	@DisplayName("Проверка CutsomerStorage на содержание всех созданных покупателей")
	void customersCheck() {
        customerStorage.addCustomer(new Customer("John", 6, 4, 50));
        customerStorage.addCustomer(new Customer("Bob", 4, 6, 200));
        customerStorage.addCustomer(new Customer("Глеб", 0, 0, 300));
        customerStorage.addCustomer(new Customer("Jack", 4, 4, 2));

        List<Customer> customers = customerStorage.getCustomers();
        for (Customer customer : customers) {
            if (customer.getName().equals("John")) {
                assertEquals(6, customer.getLegPower());
                assertEquals(4, customer.getHandPower());
                assertEquals(50, customer.getIq());
            } else if (customer.getName().equals("Bob")) {
                assertEquals(4, customer.getLegPower());
                assertEquals(6, customer.getHandPower());
                assertEquals(200, customer.getIq());
            } else if (customer.getName().equals("Глеб")) {
                assertEquals(0, customer.getLegPower());
                assertEquals(0, customer.getHandPower());
                assertEquals(300, customer.getIq());
            } else if (customer.getName().equals("Jack")) {
                assertEquals(4, customer.getLegPower());
                assertEquals(4, customer.getHandPower());
                assertEquals(2, customer.getIq());
            }
        }
	}

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

	@Test
	@DisplayName("Тест добавления машины с использованием spy-объекта")
	void testAddCarWithSpy() {
		// Создаем spy для PedalCarFactory
		PedalCarFactory pedalCarFactorySpy = spy(new PedalCarFactory());

		// Создаем реальный CarService
		CarService carService = new CarService();

		// Вызываем метод и проверяем результат
		carService.addCar(pedalCarFactorySpy, new PedalEngineParams(1));
		verify(pedalCarFactorySpy).createCar(any(), anyInt()); // Проверяем, что метод createCar был вызван
	}

	@Test
	@DisplayName("Тест с выбросом исключения в случае некорректных параметров")
	void testAddCarFails() {
		// Создаем CarService
		CarService carService = new CarService();
	
		// Пытаемся добавить машину с некорректными параметрами
		// Передаем null вместо параметров
		// Проверяем, что метод выбрасывает исключение
		assertThrows(RuntimeException.class, () -> carService.addCar(pedalCarFactory, null));
	}

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