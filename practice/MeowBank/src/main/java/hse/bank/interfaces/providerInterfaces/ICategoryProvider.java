package hse.bank.interfaces.providerInterfaces;

import java.util.List;

import hse.bank.domains.bank.Category;

public interface ICategoryProvider {
    public List<Category> getCategories();
    public Category getCategory(int id);
    public Category addCategory(String name, Boolean type);
    public Category editCategoryName(int id, String name);
    public Category editCategoryType(int id, Boolean type);
    public void deleteCategory(int id);
    public Boolean checkCategory(int id);
    public void addCategoryById(int id, String name, Boolean type);
}