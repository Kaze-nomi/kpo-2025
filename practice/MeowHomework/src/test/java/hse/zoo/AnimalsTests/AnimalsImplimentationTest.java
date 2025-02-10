package hse.zoo.AnimalsTests;

import hse.zoo.animals.Animal;
import hse.zoo.animals.Herbo;
import hse.zoo.animals.Monkey;
import hse.zoo.animals.Rabbit;
import hse.zoo.animals.Tiger;
import hse.zoo.animals.Wolf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalsImplimentationTest {
	@Test
	@DisplayName("Проверка всех методов каждой реализации Animal")
	void AnimalsImplimentationCheck() {
		Animal monkey = new Monkey(1, 1, true, 1);
		Animal rabbit = new Rabbit(1, 1, true, 1);
		Animal tiger = new Tiger(1, 1, true);
		Animal wolf = new Wolf(1, 1, true);
		
		AnimalCheck(monkey);
		AnimalCheck(rabbit);
		AnimalCheck(tiger);
		AnimalCheck(wolf);
	}
	
	private void AnimalCheck(Animal animal) {
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

        if (animal instanceof Herbo) {
            Herbo herbo = (Herbo) animal;
            assertEquals(1, herbo.getKindnessLevel());
            herbo.setKindnessLevel(2);
            assertFalse(herbo.canInteractWithVisitors());
            herbo.setKindnessLevel(10);
            assertTrue(herbo.canInteractWithVisitors());

            assertThrows(IllegalArgumentException.class, () -> herbo.setKindnessLevel(-1));
            
            assertThrows(IllegalArgumentException.class, () -> herbo.setKindnessLevel(11));
        }
    }
}