package hse.bank.FactoryTests;

import hse.bank.domains.bank.Operation;
import hse.bank.factories.bankFactories.OperationFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class OperationFactoryTest {
    @Test
    @DisplayName("Проверка всех методов OperationFactory")
    void OperationFactoryCheck() {
        OperationFactory factory = new OperationFactory();

        Date date = new Date();
        
        Operation operation = factory.createOperation(0, true, 1, 100.0, date, "Description", 2);
        
        assertEquals(date, operation.getDate());
        assertEquals(0, operation.getId());
        assertEquals("Description", operation.getDescription());
        assertEquals(100.0, operation.getAmount());
        assertEquals(2, operation.getCategory_id());
        assertEquals(1, operation.getBank_account_id());
        assertEquals(true, operation.getType());
    }
}

