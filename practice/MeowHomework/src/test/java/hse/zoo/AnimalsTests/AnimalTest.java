package hse.zoo.AnimalsTests;

import hse.zoo.animals.Animal;
import hse.zoo.animals.Herbo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
	@Test
	@DisplayName("Проверка всех методов Animal")
	void AnimalCheck() {
		Animal animal = new Herbo(1, 1, true, 1);
		assertEquals(1, animal.getFood());
		animal.setFood(2);
		assertEquals(2, animal.getFood());

		assertEquals(1, animal.getNumber());
		animal.setNumber(2);
		assertEquals(2, animal.getNumber());

		assertTrue(animal.isHealthy());
		animal.setHealthy(false);
		assertFalse(animal.isHealthy());

		assertThrows(IllegalArgumentException.class, () -> animal.setFood(-1));

        assertThrows(IllegalArgumentException.class, () -> animal.setNumber(-1));
	}
}