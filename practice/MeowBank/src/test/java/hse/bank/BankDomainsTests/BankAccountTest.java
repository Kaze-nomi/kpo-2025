package hse.bank.BankDomainsTests;

import hse.bank.domains.bank.BankAccount;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {
	@Test
	@DisplayName("Проверка всех методов BankAccount")
	void BankAccountCheck() {
		BankAccount account = new BankAccount(1, "Test", 10.0);
		
		assertEquals(1, account.getId());
		assertEquals("Test", account.getName());
		assertEquals(10.0, account.getBalance());
		
		account.setName("Test2");
		account.setBalance(20.0);
		
		assertEquals("Test2", account.getName());
		assertEquals(20.0, account.getBalance());
	}
}
