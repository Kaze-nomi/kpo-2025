package hse.bank.BankDomainsTests;

import hse.bank.domains.bank.Operation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class OperationTest {
	@Test
	@DisplayName("Проверка всех методов Operation")
	    void OperationCheck() {

            Date date = new Date();

            Operation operation = new Operation(0, true, 1, 100.0, date, "Description", 2);
            
            assertEquals(0, operation.getId());
            assertEquals("Description", operation.getDescription());
            assertEquals(date, operation.getDate());
            assertEquals(100.0, operation.getAmount());
            assertEquals(2, operation.getCategory_id());
            assertEquals(1, operation.getBank_account_id());
            assertEquals(true, operation.getType());
            operation.setDescription("UpdatedDescription");
            assertEquals("UpdatedDescription", operation.getDescription());
    }
}
