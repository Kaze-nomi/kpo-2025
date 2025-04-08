package hse.bank.factories.bankFactories;

import hse.bank.domains.bank.Category;

import org.springframework.stereotype.Component;

@Component
public class CategoryFactory {
    public Category createCategory(int id, Boolean type, String name) {
        return new Category(id, type, name);
    }
}