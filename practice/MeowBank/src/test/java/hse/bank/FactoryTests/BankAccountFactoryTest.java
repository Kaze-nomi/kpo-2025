package hse.bank.FactoryTests;

import hse.bank.domains.bank.BankAccount;

import hse.bank.factories.bankFactories.BankAccountFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountFactoryTest {
	@Test
	@DisplayName("Проверка всех методов BankAccountFactory")
	void BankAccountFactoryCheck() {
		BankAccountFactory factory = new BankAccountFactory();
		
		BankAccount account = factory.createBankAccount(1, "Test", 100.0);
		
		assertEquals(1, account.getId());
		assertEquals("Test", account.getName());
		assertEquals(100.0, account.getBalance());
	}
}

