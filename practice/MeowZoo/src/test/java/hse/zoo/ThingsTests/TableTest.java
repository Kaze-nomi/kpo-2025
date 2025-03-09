package hse.zoo.ThingsTests;

import hse.zoo.things.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {
	@Test
	@DisplayName("Проверка всех методов Table")
	void TableCheck() {
        Table table = new Table(1);

        assertEquals(1, table.getNumber());
        table.setNumber(2);
        assertEquals(2, table.getNumber());

        assertThrows(IllegalArgumentException.class, () -> table.setNumber(-1));
	}
}