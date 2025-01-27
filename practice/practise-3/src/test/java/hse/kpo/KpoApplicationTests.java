package hse.kpo;

import hse.kpo.domains.Customer;
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

@SpringBootTest
class KpoApplicationTests {

	@Autowired
	private CarService carService;

	@Autowired
	private CustomerStorage customerStorage;

	@Autowired
	private HseCarService hseCarService;

	@Autowired
	private PedalCarFactory pedalCarFactory;

	@Autowired
	private HandCarFactory handCarFactory;

	@Autowired
	private LevitationCarFactory levitationCarFactory;

	@Test
	@DisplayName("Тест загрузки контекста")
	void contextLoads() {
		SpringApplication.run(KpoApplication.class, args);
        
        customerStorage.addCustomer(new Customer("Customer 1", 6, 4, 50));
        customerStorage.addCustomer(new Customer("Customer 2", 4, 6, 200));
        customerStorage.addCustomer(new Customer("Customer 3", 0, 0, 300));
        customerStorage.addCustomer(new Customer("Customer 4", 4, 4, 2));
        
        carService.addCar(pedalCarFactory, new PedalEngineParams(1));
        carService.addCar(pedalCarFactory, new PedalEngineParams(2));
        carService.addCar(handCarFactory, new EmptyEngineParams());
        carService.addCar(handCarFactory, new EmptyEngineParams());
        carService.addCar(levitationCarFactory, new EmptyEngineParams());

		carService.addCar(handCarFactory, EmptyEngineParams.DEFAULT);
		carService.addCar(handCarFactory, EmptyEngineParams.DEFAULT);

		customerStorage.getCustomers().stream().map(Customer::toString).forEach(System.out::println);

		hseCarService.sellCars();

		customerStorage.getCustomers().stream().map(Customer::toString).forEach(System.out::println);
	}
}