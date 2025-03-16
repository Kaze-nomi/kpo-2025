package hse.bank.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import hse.bank.interfaces.observerInterfaces.IOperationObserver;
import hse.bank.interfaces.providerInterfaces.IBankAccountProvider;
import hse.bank.interfaces.providerInterfaces.ICategoryProvider;
import hse.bank.interfaces.providerInterfaces.IOperationProvider;
import hse.bank.domains.bank.Operation;
import hse.bank.factories.bankFactories.OperationFactory;

@Component
@RequiredArgsConstructor
public class OperationService implements IOperationProvider {
    
    final List<IOperationObserver> observers = new ArrayList<>();

    @Getter
    private final IBankAccountProvider bankAccountService;

    @Getter
    private final ICategoryProvider categoryService;

    @Getter
    private final OperationFactory operationFactory;

    @Getter
    private final List<Operation> operations = new ArrayList<>();

    private int operationNumberCounter = 0;

    @Override
    public Operation addOperation(int bank_account_id, double amount, Date date, String description, Boolean type, int category_id) {
        while (operations.stream().anyMatch(o -> o.getId() == operationNumberCounter)) {
            operationNumberCounter++;
        } 
        
        var bank_account_exists = bankAccountService.checkAccount(bank_account_id);
        if (bank_account_exists == false) {
            System.out.println("Bank account with id %d does not exists".formatted(bank_account_id));
            return null;
        }
        
        var category_exists = categoryService.checkCategory(category_id);
        if (category_exists == false) {
            System.out.println("Category with id %d does not exists".formatted(category_id));
            return null;
        }

        if (type == true) {
            bankAccountService.editAccountBalance(bank_account_id, amount);
        } else {
            bankAccountService.editAccountBalance(bank_account_id, -amount);
        }

        var operation = operationFactory.createOperation(operationNumberCounter++, type, bank_account_id, amount, date, description, category_id);
        operations.add(operation);

        return operation;
    }

    @Override
    public Operation editOperationDescription(int id, String description) {
        var operation = operations.stream().filter(o -> o.getId() == id).findFirst();
        if (operation.isPresent()) {
            operation.get().setDescription(description);
            return operation.get();
        } else {
            System.out.println("Operation with id %d does not exists".formatted(id));
            return null;
        }
    }

    @Override
    public Operation editOperationCategory (int id, int category_id) {
        var category_exists = categoryService.checkCategory(category_id);
        if (category_exists == false) {
            System.out.println("Category with id %d does not exists".formatted(category_id));
            return null;
        }

        var operation = operations.stream().filter(o -> o.getId() == id).findFirst();
        if (operation.isPresent()) {
            if (operation.get().getType() != categoryService.getCategory(category_id).getType()) {
                bankAccountService.editAccountBalance(operation.get().getBank_account_id(), -operation.get().getAmount());
            }
            operation.get().setType(categoryService.getCategory(category_id).getType());
            operation.get().setCategory_id(category_id);
            return operation.get();
        } else {
            System.out.println("Operation with id %d does not exists".formatted(id));
            return null;
        }
    }

    @Override
    public void deleteOperation(int id) {
        var operation = operations.stream().filter(o -> o.getId() == id).findFirst();
        if (operation.isPresent()) {
            bankAccountService.editAccountBalance(operation.get().getBank_account_id(), -operation.get().getAmount());
            operations.remove(operation.get());
        } else {
            System.out.println("Operation with id %d does not exists".formatted(id));
        }
    }

    @Override
    public void deleteOperationsByBankAccountId(int id) {
        operations.removeIf(o -> o.getBank_account_id() == id);
    }

    @Override
    public Operation getOperation(int id) {
        var operation = operations.stream().filter(o -> o.getId() == id).findFirst();
        if (operation.isPresent()) {
            return operation.get();
        } else {
            System.out.println("Operation with id %d does not exists".formatted(id));
            return null;
        }
    }

    @Override
    public Boolean checkOperation (int cat_it) {
        var operation = operations.stream().filter(o -> o.getCategory_id() == cat_it).findFirst();
        if (operation.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addOperationById(int id, int bank_account_id, double amount, Date date, String description, Boolean type, int category_id) {
        var operation = operationFactory.createOperation(id, type, bank_account_id, amount, date, description, category_id);
        operations.add(operation);
    }
}