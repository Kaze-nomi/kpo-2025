package hse.kpo.FactoriesTests;

import hse.kpo.domains.Car;
import hse.kpo.domains.Ship;
import hse.kpo.domains.HandEngine;
import hse.kpo.domains.LevitationEngine;
import hse.kpo.domains.PedalEngine;
import hse.kpo.factories.HandCarFactory;
import hse.kpo.factories.PedalCarFactory;
import hse.kpo.factories.LevitationCarFactory;
import hse.kpo.factories.PedalShipFactory;
import hse.kpo.factories.LevitationShipFactory;
import hse.kpo.factories.HandShipFactory;
import hse.kpo.params.EmptyEngineParams;
import hse.kpo.params.PedalEngineParams;
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

	@Autowired
	private PedalShipFactory pedalShipFactory;

	@Autowired
	private HandShipFactory handShipFactory;

	@Autowired
	private LevitationShipFactory levitationShipFactory;

	@Test
	@DisplayName("Проверка каждой фабрики на соответствие типу производимого двигателя")
	void factoryCheck() {
		Car pedalCar = pedalCarFactory.createCar(new PedalEngineParams(1), 1);
		Car handCar = handCarFactory.createCar(EmptyEngineParams.DEFAULT, 2);
		Car levitationCar = levitationCarFactory.createCar(EmptyEngineParams.DEFAULT, 3);

		assertTrue(pedalCar.getEngine() instanceof PedalEngine);
		assertTrue(handCar.getEngine() instanceof HandEngine);
		assertTrue(levitationCar.getEngine() instanceof LevitationEngine);
		
		Ship pedalShip = pedalShipFactory.createShip(new PedalEngineParams(5), 1);
		Ship handShip = handShipFactory.createShip(EmptyEngineParams.DEFAULT, 2);
		Ship levitationShip = levitationShipFactory.createShip(EmptyEngineParams.DEFAULT, 3);

		assertTrue(pedalShip.getEngine() instanceof PedalEngine);
		assertTrue(handShip.getEngine() instanceof HandEngine);
		assertTrue(levitationShip.getEngine() instanceof LevitationEngine);
	}
}