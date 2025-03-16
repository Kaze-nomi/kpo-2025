package hse.bank.FacadeTests;

import hse.bank.domains.bank.BankAccount;
import hse.bank.domains.bank.Category;
import hse.bank.domains.bank.Operation;
import hse.bank.facade.HSEBank;
import hse.bank.params.ReportFormat;
import jakarta.annotation.PreDestroy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HSEBankTest {

    @Autowired
    private HSEBank bank;

    private final Date testDate = new Date();

    @Test
    @DisplayName("Полное тестирование всех методов фасада")
    void fullFacadeTest() {
        // Тестирование создания сущностей
        testAccountAndCategoryCreation();
        
        // Тестирование операций
        testOperations();
        
        // Тестирование модификаций
        testEdits();
        
        // Тестирование удалений
        testDeletions();

        // Тестирование отчетов и экспорта
        testReportsAndExport();
        
        // Тестирование дополнительных методов
        testAdditionalMethods();

        // Удаление тестовых файлов
        deleteTestFiles();
    }

    private void testAccountAndCategoryCreation() {
        // Создание аккаунта
        bank.createBankAccount("Account0");
        BankAccount acc = bank.getOperationService().getBankAccountService().getAccount(0);
        assertNotNull(acc);
        assertEquals("Account0", acc.getName());

        // Создание категории
        bank.createCategory("Income", true);
        Category cat = bank.getOperationService().getCategoryService().getCategory(0);
        assertNotNull(cat);
        assertTrue(cat.getType());
    }

    private void testOperations() {
        // Добавление операции
        bank.createBankAccount("Wallet");
        bank.createCategory("Food", false);
        bank.addOperation(0, 500.0, testDate, "Lunch", 0);
        
        Operation op = bank.getOperationService().getOperations().get(0);
        assertEquals(500.0, op.getAmount());
        assertEquals("Lunch", op.getDescription());
    }

    private void testEdits() {
        // Изменение описания операции
        bank.createBankAccount("Acc");
        bank.createCategory("Cat", true);
        bank.addOperation(0, 100.0, testDate, "OldDesc", 0);
        bank.editOperationDescription(0, "NewDesc");
        
        assertEquals("NewDesc", bank.getOperationService().getOperation(0).getDescription());

        // Изменение категории операции
        bank.createCategory("NewCat", true);
        bank.editOperationCategory(0, 1);
        assertEquals(1, bank.getOperationService().getOperation(0).getCategory_id());

        // Изменение типа категории
        bank.editCategoryType(0, false);
        assertFalse(bank.getOperationService().getCategoryService().getCategory(0).getType());
    }

    private void testDeletions() {
        // Удаление аккаунта с операциями
        bank.createBankAccount("ToDelete");
        bank.createCategory("TempCat", true);
        bank.addOperation(0, 100.0, testDate, "TempOp", 0);
        bank.deleteOperation(0);
        bank.deleteAccount(3);
        bank.deleteAccount(2);
        bank.deleteAccount(1);
        bank.deleteAccount(0);

        
        assertTrue(bank.getOperationService().getBankAccountService().getAccounts().isEmpty());
        assertTrue(bank.getOperationService().getOperations().isEmpty());

        // Попытка удаления категории с операциями
        bank.createCategory("UsedCat", true);
        bank.createBankAccount("Acc");
        bank.addOperation(4, 100.0, testDate, "Test", 0);
        bank.deleteCategory(0);
        assertFalse(bank.getOperationService().getCategoryService().getCategories().isEmpty());
    }

    private void testReportsAndExport() {
        // Проверка построения отчета
        bank.createBankAccount("RepAcc");
        bank.createCategory("RepCat", true);
        bank.addOperation(5, 1000.0, testDate, "ReportOp", 0);
        assertNotNull(bank.buildReport());

        // Проверка экспорта/импорта
        testExportImport();
    }

    private void testExportImport() {
        bank.deleteAccount(4);
        bank.deleteAccount(5);
        bank.deleteCategory(0);
        bank.deleteCategory(1);
        bank.deleteCategory(2);
        bank.deleteCategory(3);
        bank.deleteCategory(4);
        bank.deleteCategory(5);
        bank.deleteCategory(6);

        // Экспорт данных
        bank.createBankAccount("ExportAcc");
        bank.createCategory("ExportCat", false);
        bank.addOperation(6, 100.0, testDate, "ExportOp", 7);
        bank.destroy();

        bank.deleteAccount(6);
        bank.deleteCategory(7);
        bank.deleteOperation(6);

        // Импорт данных
        bank.init(ReportFormat.JSON);

        assertEquals(1, bank.getOperationService().getBankAccountService().getAccounts().size());
        assertEquals(1, bank.getOperationService().getCategoryService().getCategories().size());
        assertEquals(1, bank.getOperationService().getOperations().size());
        assertEquals(-100.0, bank.getOperationService().getBankAccountService().getAccount(6).getBalance());

        bank.deleteAccount(6);
        bank.deleteCategory(7);

        bank.init(ReportFormat.CSV);

        assertEquals(1, bank.getOperationService().getBankAccountService().getAccounts().size());
        assertEquals(1, bank.getOperationService().getCategoryService().getCategories().size());
        assertEquals(1, bank.getOperationService().getOperations().size());
        assertEquals(-100.0, bank.getOperationService().getBankAccountService().getAccount(6).getBalance());

        bank.deleteAccount(6);
        bank.deleteCategory(7);

        bank.init(ReportFormat.YAML);
        
        assertEquals(1, bank.getOperationService().getBankAccountService().getAccounts().size());
        assertEquals(1, bank.getOperationService().getCategoryService().getCategories().size());
        assertEquals(1, bank.getOperationService().getOperations().size());
        assertEquals(-100.0, bank.getOperationService().getBankAccountService().getAccount(6).getBalance());
    }

    private void testAdditionalMethods() {
        // Тестирование вспомогательных методов
        bank.createBankAccount("Acc0");
        bank.createBankAccount("Acc1");
        bank.countAllMoney(); // Проверка вызова без ошибок

        // Проверка вывода в консоль (можно перенаправить System.out для проверки)
        bank.printAccounts();
        bank.printCategories();
        bank.printOperations();

        bank.deleteAccount(6);
        bank.deleteAccount(7);
        bank.deleteAccount(8);
        bank.deleteCategory(7);

        bank = null;

        deleteTestFiles();
    }

    @PreDestroy
    private void deleteTestFiles() {
        String[] formats = {"json", "csv", "yaml"};
        for (String format : formats) {
            File file = new File("./record/data." + format);
            if (file.exists()) file.delete();
        }
        
        File report = new File("./report/history.log");
        if (report.exists()) report.delete();
    }
}