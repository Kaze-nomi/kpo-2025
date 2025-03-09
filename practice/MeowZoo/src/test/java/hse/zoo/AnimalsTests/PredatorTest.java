package hse.zoo.AnimalsTests;

import hse.zoo.animals.Predator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PredatorTest {
	@Test
	@DisplayName("Проверка всех методов Predator")
	void PredatorCheck() {
		Predator predator = new Predator(1, 1, true);

		assertEquals(1, predator.getFood());
		predator.setFood(2);
		assertEquals(2, predator.getFood());
		assertTrue(predator.isHealthy());
		predator.setHealthy(false);
		assertFalse(predator.isHealthy());

		assertThrows(IllegalArgumentException.class, () -> predator.setFood(-1));

		assertThrows(IllegalArgumentException.class, () -> predator.setNumber(-1));
	}
}