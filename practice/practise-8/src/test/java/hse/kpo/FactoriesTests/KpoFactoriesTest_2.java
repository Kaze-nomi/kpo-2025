package hse.kpo.FactoriesTests;

import hse.kpo.factories.carFactories.PedalCarFactory;
import hse.kpo.params.PedalEngineParams;
import hse.kpo.services.CarService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class KpoFactoriesTest_2 {
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
}
