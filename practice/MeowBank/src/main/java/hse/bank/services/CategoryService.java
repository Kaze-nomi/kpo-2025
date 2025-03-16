package hse.bank.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import hse.bank.domains.bank.Category;
import hse.bank.factories.bankFactories.CategoryFactory;
import hse.bank.interfaces.providerInterfaces.ICategoryProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryService implements ICategoryProvider {

    private int categoryNumberCounter = 0;

    @Getter
    private final CategoryFactory categoryFactory;

    @Getter
    private final List<Category> categories = new ArrayList<>();

    @Override
    public Category addCategory(String name, Boolean type) {
        while (checkCategory(categoryNumberCounter)) {
            categoryNumberCounter++;
        }
        var category = categoryFactory.createCategory(categoryNumberCounter++, type, name);
        categories.add(category);
        return category;
    }

    @Override
    public Category editCategoryName(int id, String name) {
        var category = categories.stream().filter(a -> a.getId() == id).findFirst();
        if (category.isPresent()) {
            category.get().setName(name);
            return category.get();
        } else {
            System.out.println("Category with id %d does not exists".formatted(id));
            return null;
        }
    }

    @Override
    public Category editCategoryType(int id, Boolean type) {
        var category = categories.stream().filter(a -> a.getId() == id).findFirst();
        if (category.isPresent()) {
            category.get().setType(type);
            return category.get();
        } else {
            System.out.println("Category with id %d does not exists".formatted(id));
            return null;
        }
    }

    @Override
    public void deleteCategory(int id) {
        var category = categories.stream().filter(a -> a.getId() == id).findFirst();
        if (category.isPresent()) {
            categories.remove(category.get());
        } else {
            System.out.println("Category with id %d does not exists".formatted(id));
        }
    }

    @Override
    public Boolean checkCategory(int id) {
        var category = categories.stream().filter(a -> a.getId() == id).findFirst();
        if (category.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Category getCategory(int id) {
        var category = categories.stream().filter(a -> a.getId() == id).findFirst();
        if (category.isPresent()) {
            return category.get();
        } else {
            System.out.println("Category with id %d does not exists".formatted(id));
            return null;
        }
    }

    @Override
    public void addCategoryById(int id, String name, Boolean type) {
        var category = categoryFactory.createCategory(id, type, name);
        categories.add(category);
    }
}