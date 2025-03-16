package hse.bank.FactoryTests;

import hse.bank.domains.bank.Category;
import hse.bank.factories.bankFactories.CategoryFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryFactoryTest {
	@Test
	@DisplayName("Проверка всех методов CategoryFactory")
	void CategoryFactoryCheck() {
		CategoryFactory factory = new CategoryFactory();
		
		Category category = factory.createCategory(1, true, "Test");
		
		assertEquals(1, category.getId());
		assertEquals("Test", category.getName());
		assertEquals(true, category.getType());
	}
}