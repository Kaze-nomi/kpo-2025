package hse.bank.BankDomainsTests;

import hse.bank.domains.bank.Category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {
    @Test
    @DisplayName("Проверка всех методов Category")
    void CategoryCheck() {
        Category category = new Category(0, true, "TestCategory");
        assertEquals(0, category.getId());
        assertEquals("TestCategory", category.getName());
		assertEquals(true, category.getType());

        category.setName("UpdatedCategory");

        assertEquals("UpdatedCategory", category.getName());

	}
}
