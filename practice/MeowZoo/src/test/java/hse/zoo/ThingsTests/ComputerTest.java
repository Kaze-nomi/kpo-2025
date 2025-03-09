package hse.zoo.ThingsTests;

import hse.zoo.things.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ComputerTest {
	@Test
	@DisplayName("Проверка всех методов Computer")
	void ComputerCheck() {
        Computer computer = new Computer(1);

        assertEquals(1, computer.getNumber());
        computer.setNumber(2);
        assertEquals(2, computer.getNumber());

        assertThrows(IllegalArgumentException.class, () -> computer.setNumber(-1));
	}
}