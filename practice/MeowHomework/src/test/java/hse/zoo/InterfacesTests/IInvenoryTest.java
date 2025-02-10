package hse.zoo.InterfacesTests;

import hse.zoo.interfaces.IInventory;
import hse.zoo.things.Table;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IInventoryTest {
    @Test
    @DisplayName("Проверка интерфейса IInventory")
    void IInventorycheck() {
        IInventory table = new Table(1);
        assertEquals(1, table.getNumber());
        table.setNumber(2);
        assertEquals(2, table.getNumber());

        assertThrows(IllegalArgumentException.class, () -> table.setNumber(-1));
    }
}