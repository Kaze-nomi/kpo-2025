package hse.kpo.ServicesTests;

import hse.kpo.factories.PedalCarFactory;
import hse.kpo.services.CarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KpoServicesTest_3 {

    @Autowired
    private PedalCarFactory pedalCarFactory;

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
}