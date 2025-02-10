package hse.zoo.InterfacesTests;

import hse.zoo.animals.Herbo;
import hse.zoo.interfaces.IAlive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IAliveTest {
    @Test
    @DisplayName("Проверка интерфейса IAlive")
    void IAlivecheck() {
        IAlive herbo = new Herbo(1, 1, true, 1);
        
        assertEquals(1, herbo.getFood());
        herbo.setFood(2);
        assertEquals(2, herbo.getFood());
        
        assertThrows(IllegalArgumentException.class, () -> herbo.setFood(-1));
    }
}