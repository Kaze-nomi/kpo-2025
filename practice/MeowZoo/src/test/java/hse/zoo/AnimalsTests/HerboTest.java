package hse.zoo.AnimalsTests;

import hse.zoo.animals.Herbo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HerboTest {
	@Test
	@DisplayName("Проверка всех методов Herbo")
	void HerboCheck() {
		Herbo herbo = new Herbo(1, 1, true, 1);

		assertEquals(1, herbo.getKindnessLevel());
		herbo.setKindnessLevel(2);
		assertEquals(2, herbo.getKindnessLevel());

		assertThrows(IllegalArgumentException.class, () -> herbo.setFood(-1));

		assertThrows(IllegalArgumentException.class, () -> herbo.setNumber(-1));

		assertThrows(IllegalArgumentException.class, () -> herbo.setKindnessLevel(-1));

		assertThrows(IllegalArgumentException.class, () -> herbo.setKindnessLevel(11));
	}
}