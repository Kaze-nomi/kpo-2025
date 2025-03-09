package hse.zoo.ThingsTests;

import hse.zoo.things.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThingTest {
	@Test
	@DisplayName("Проверка всех методов Thing")
	void ThingCheck() {
        Thing thing = new Thing(1);

        assertEquals(1, thing.getNumber());
        thing.setNumber(2);
        assertEquals(2, thing.getNumber());

        assertThrows(IllegalArgumentException.class, () -> thing.setNumber(-1));
	}
}