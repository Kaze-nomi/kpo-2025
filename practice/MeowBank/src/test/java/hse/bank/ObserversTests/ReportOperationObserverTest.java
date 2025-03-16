package hse.bank.ObserversTests;

import hse.bank.domains.bank.BankAccount;
import hse.bank.domains.bank.Category;
import hse.bank.domains.bank.Operation;
import hse.bank.observers.ReportOperationObserver;
import hse.bank.params.OperationType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportOperationObserverTest {
    @Test
    @DisplayName("Проверка всех методов ReportOperationObserver")
    void ReportOperationObserverCheck() {
        ReportOperationObserver observer = new ReportOperationObserver();
        Date date = new Date();

        BankAccount account = new BankAccount(1, "TestAccount", 100);
        BankAccount renamedAccount = new BankAccount(1, "RenamedAccount", 100);
        Category category = new Category(1, true, "TestCategory");
        Category renamedCategory = new Category(1, true, "RenamedCategory");
        Category typeChangedCategory = new Category(1, false, "TestCategory");
        Operation operation = new Operation(1, true, 1, 100.0, date, "TestOperation", 1);
        Operation editedDescOperation = new Operation(1, true, 1, 100.0, date, "NewDescription", 1);

        observer.onOperation(account, null, null, OperationType.ADD_ACC);
        observer.onOperation(account, null, null, OperationType.REMOVE_ACC);
        observer.onOperation(renamedAccount, null, null, OperationType.EDIT_ACC_NAME);
        
        observer.onOperation(null, category, null, OperationType.ADD_CAT);
        observer.onOperation(null, category, null, OperationType.REMOVE_CAT);
        observer.onOperation(null, renamedCategory, null, OperationType.EDIT_CAT_NAME);
        observer.onOperation(null, typeChangedCategory, null, OperationType.EDIT_CAT_TYPE);
        
        observer.onOperation(account, category, operation, OperationType.ADD_OP);
        observer.onOperation(null, null, operation, OperationType.REMOVE_OP);
        observer.onOperation(null, null, editedDescOperation, OperationType.EDIT_OP_DESC);
        observer.onOperation(null, renamedCategory, operation, OperationType.EDIT_OP_CAT);

        assertEquals(
            "[Создание] Аккаунт: TestAccount (ID: 1)",
            observer.getMessages().get(0)
        );
        
        assertEquals(
            "[Удаление] Аккаунт: TestAccount (ID: 1) | Все операции привязанные к аккаунты были удалены",
            observer.getMessages().get(1)
        );
        
        assertEquals(
            "[Изменение] Аккаунт ID 1: новое имя - 'RenamedAccount'",
            observer.getMessages().get(2)
        );
        
        assertEquals(
            "[Создание] Категория: TestCategory (ID: 1, Тип: Доход)",
            observer.getMessages().get(3)
        );
        
        assertEquals(
            "[Удаление] Категория: TestCategory (ID: 1)",
            observer.getMessages().get(4)
        );
        
        assertEquals(
            "[Изменение] Категория ID 1: новое имя - 'RenamedCategory'",
            observer.getMessages().get(5)
        );
        
        assertEquals(
            "[Изменение] Категория 'TestCategory' (ID: 1): новый тип - Расход | Типы операций привязанных к категории были пересчитаны | Балансы аккаунтов привязанные к операциям пересчитаны",
            observer.getMessages().get(6)
        );
        
        assertEquals(
            String.format("[Добавление] Операция ID 1: Доход 100.00 ₽ | Аккаунт: TestAccount (ID: 1) | Категория: TestCategory (ID: 1) | TestOperation"),
            observer.getMessages().get(7)
        );
        
        assertEquals(
            "[Удаление] Операция ID 1: (Доход 100.00 ₽) удалена",
            observer.getMessages().get(8)
        );
        
        assertEquals(
            "[Изменение] Операция ID 1: новое описание - 'NewDescription'",
            observer.getMessages().get(9)
        );
        
        assertEquals(
            "[Изменение] Операция ID 1: категория изменена на 'RenamedCategory' | Тип операции пересчитан | Баланс аккаунта с ID 1 пересчитан",
            observer.getMessages().get(10)
        );
    }
}