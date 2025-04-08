package hse.bank.observers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import hse.bank.builders.Report;
import hse.bank.builders.ReportBuilder;
import hse.bank.domains.bank.BankAccount;
import hse.bank.domains.bank.Category;
import hse.bank.domains.bank.Operation;
import hse.bank.interfaces.observerInterfaces.IOperationObserver;
import hse.bank.params.OperationType;

@Component
@RequiredArgsConstructor
public class ReportOperationObserver implements IOperationObserver {

    @Getter
    private final List<String> messages = new ArrayList<>();

    private final ReportBuilder reportBuilder = new ReportBuilder();

    public Report buildReport() {
        return reportBuilder.build();
    }

    @Override
    public void onOperation(BankAccount account, Category category, Operation operation, OperationType type) {
        String message = "";
        switch (type) {
            // Операции с операциями
            case ADD_OP:
                message = String.format("[Добавление] Операция ID %d: %s %.2f ₽ | Аккаунт: %s (ID: %d) | Категория: %s (ID: %d) | %s",
                        operation.getId(),        
                        operation.getType() ? "Доход" : "Расход",
                        operation.getAmount(),
                        account.getName(),
                        account.getId(),
                        category.getName(),
                        category.getId(),
                        operation.getDescription());
                break;
                
            case REMOVE_OP:
                message = String.format("[Удаление] Операция ID %d: (%s %.2f ₽) удалена", 
                        operation.getId(),
                        operation.getType() ? "Доход" : "Расход",
                        operation.getAmount());
                break;
                
            case EDIT_OP_DESC:
                message = String.format("[Изменение] Операция ID %d: новое описание - '%s'", 
                        operation.getId(), 
                        operation.getDescription());
                break;
                
            case EDIT_OP_CAT:
                message = String.format("[Изменение] Операция ID %d: категория изменена на '%s' | Тип операции пересчитан | Баланс аккаунта с ID %d пересчитан", 
                        operation.getId(), 
                        category.getName(),
                        operation.getBank_account_id());
                break;

            // Операции с аккаунтами
            case ADD_ACC:
                message = String.format("[Создание] Аккаунт: %s (ID: %d)", 
                        account.getName(), 
                        account.getId());
                break;
                
            case REMOVE_ACC:
                message = String.format("[Удаление] Аккаунт: %s (ID: %d) | Все операции привязанные к аккаунты были удалены", 
                        account.getName(), 
                        account.getId());
                break;
                
            case EDIT_ACC_NAME:
                message = String.format("[Изменение] Аккаунт ID %d: новое имя - '%s'", 
                        account.getId(), 
                        account.getName());
                break;

            // Операции с категориями
            case ADD_CAT:
                message = String.format("[Создание] Категория: %s (ID: %d, Тип: %s)", 
                        category.getName(), 
                        category.getId(),
                        category.getType() ? "Доход" : "Расход");
                break;
                
            case REMOVE_CAT:
                message = String.format("[Удаление] Категория: %s (ID: %d)", 
                        category.getName(),
                        category.getId());
                break;
                
            case EDIT_CAT_NAME:
                message = String.format("[Изменение] Категория ID %d: новое имя - '%s'", 
                        category.getId(), 
                        category.getName());
                break;
                
            case EDIT_CAT_TYPE:
                message = String.format("[Изменение] Категория '%s' (ID: %d): новый тип - %s | Типы операций привязанных к категории были пересчитаны | Балансы аккаунтов привязанные к операциям пересчитаны", 
                        category.getName(), 
                        category.getId(),
                        category.getType() ? "Доход" : "Расход");
                break;
        }
        messages.add(message);
        reportBuilder.addOperation(message);
    }
}