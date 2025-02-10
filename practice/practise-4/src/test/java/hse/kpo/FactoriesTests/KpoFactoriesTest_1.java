package hse.kpo.FactoriesTests;

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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KpoFactoriesTest_1 {

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
}