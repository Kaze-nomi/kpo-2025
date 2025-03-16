package hse.bank.facade;

import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.springframework.stereotype.Component;

import hse.bank.builders.Report;
import hse.bank.domains.bank.*;
import hse.bank.factories.exportFactories.DataExporterFactory;
import hse.bank.factories.importFactories.DataImporterFactory;
import hse.bank.interfaces.exporterInterfaces.IDataExporter;
import hse.bank.interfaces.importerInterfaces.IDataImporter;
import hse.bank.interfaces.providerInterfaces.IOperationProvider;
import hse.bank.observers.ReportOperationObserver;
import hse.bank.params.OperationType;
import hse.bank.params.ReportFormat;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HSEBank {

    @Getter
    private final IOperationProvider operationService;

    private final ReportOperationObserver reportOperationObserver;

    private final DataExporterFactory dataExporterFactory;

    private final DataImporterFactory dataImporterFactory;

    @PreDestroy
    public void destroy() {
        saveData(ReportFormat.JSON);
        saveData(ReportFormat.CSV);
        saveData(ReportFormat.YAML);
        
        File dir = new File("./report");
        if (!dir.exists()) {
            dir.mkdirs();
        }   
    
        try (FileWriter fileWriter = new FileWriter("./report/history.log", true)) {
			fileWriter.append(buildReport().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void init(ReportFormat format) {
        loadData(format);
    }

    public void createBankAccount(String name) {
        BankAccount account = operationService.getBankAccountService().addAccount(name);
        if (account != null) {
            notifyObserverForOperation(account, null, null, OperationType.ADD_ACC);
        }
    }

    public void createCategory(String name, Boolean type) {
        Category category = operationService.getCategoryService().addCategory(name, type);
        if (category != null) {
            notifyObserverForOperation(null, category, null, OperationType.ADD_CAT);
        }
    }

    public void addOperation(int bank_account_id, double amount, Date date, String description, int category_id) {
        var operation = operationService.addOperation(bank_account_id, amount, date, description, operationService.getCategoryService().getCategory(category_id).getType(), category_id);
        if (operation != null) {
            notifyObserverForOperation(
                operationService.getBankAccountService().getAccount(bank_account_id),
                operationService.getCategoryService().getCategory(category_id),
                operation, 
                OperationType.ADD_OP
            );
        }
    }

    public void editOperationDescription(int id, String description) {
        Operation operation = operationService.getOperation(id);
        if (operation != null) {
            BankAccount account = operationService.getBankAccountService().getAccount(operation.getBank_account_id());
            Category category = operationService.getCategoryService().getCategory(operation.getCategory_id());
            operationService.editOperationDescription(id, description);
            notifyObserverForOperation(account, category, operation, OperationType.EDIT_OP_DESC);
        }
    }

    public void editOperationCategory(int id, int category_id) {
        Operation operation = operationService.getOperation(id);
        if (operation != null) {
            BankAccount account = operationService.getBankAccountService().getAccount(operation.getBank_account_id());
            operationService.editOperationCategory(id, category_id);
            Category newCategory = operationService.getCategoryService().getCategory(category_id);
            Operation updatedOperation = operationService.getOperation(id);
            notifyObserverForOperation(account, newCategory, updatedOperation, OperationType.EDIT_OP_CAT);
        }
    }

    public void editAccountName(int id, String name) {
        BankAccount account = operationService.getBankAccountService().getAccount(id);
        if (account != null) {
            operationService.getBankAccountService().editAccountName(id, name);
            BankAccount updatedAccount = operationService.getBankAccountService().getAccount(id);
            notifyObserverForOperation(updatedAccount, null, null, OperationType.EDIT_ACC_NAME);
        }
    }

    public void editCategoryName(int id, String name) {
        Category category = operationService.getCategoryService().getCategory(id);
        if (category != null) {
            operationService.getCategoryService().editCategoryName(id, name);
            Category updatedCategory = operationService.getCategoryService().getCategory(id);
            notifyObserverForOperation(null, updatedCategory, null, OperationType.EDIT_CAT_NAME);
        }
    }

    public void editCategoryType(int id, Boolean type) {
        Category category = operationService.getCategoryService().getCategory(id);
        if (category != null) {
            operationService.getCategoryService().editCategoryType(id, type);
            Category updatedCategory = operationService.getCategoryService().getCategory(id);
            operationService.getOperations().forEach(op -> {
                if (op.getCategory_id() == id) {
                    op.setType(type);
                    BankAccount account = operationService.getBankAccountService().getAccount(op.getBank_account_id());
                    if (type == true) {
                        operationService.getBankAccountService().editAccountBalance(account.getId(), op.getAmount());
                    } else {
                        operationService.getBankAccountService().editAccountBalance(account.getId(), -op.getAmount());
                    }
                }
            });
            notifyObserverForOperation(null, updatedCategory, null, OperationType.EDIT_CAT_TYPE);
        }
    }

    public void deleteOperation(int id) {
        Operation operation = operationService.getOperation(id);
        if (operation != null) {
            BankAccount account = operationService.getBankAccountService().getAccount(operation.getBank_account_id());
            Category category = operationService.getCategoryService().getCategory(operation.getCategory_id());
            operationService.deleteOperation(id);
            notifyObserverForOperation(account, category, operation, OperationType.REMOVE_OP);
        }
    }

    public void deleteAccount(int id) {
        BankAccount account = operationService.getBankAccountService().getAccount(id);
        if (account != null) {
            operationService.deleteOperationsByBankAccountId(id);
            operationService.getBankAccountService().deleteAccount(id);
            notifyObserverForOperation(account, null, null, OperationType.REMOVE_ACC);
        }
    }

    public void deleteCategory(int id) {
        Category category = operationService.getCategoryService().getCategory(id);
        if (category != null) {
            Boolean exist = operationService.checkOperation(id);
            if (exist) {
                System.out.println("Category with id %d is connected to some operation and cannot be deleted".formatted(id));
            } else {
                operationService.getCategoryService().deleteCategory(id);
                notifyObserverForOperation(null, category, null, OperationType.REMOVE_CAT);
            }
        }
    }

    public void printAccounts() {
        List<BankAccount> accounts = operationService.getBankAccountService().getAccounts();
        for (BankAccount account : accounts) {
            System.out.println("ID аккаунта: " + account.getId());
            System.out.println("Имя аккаунта: " + account.getName());
            System.out.println("Баланс аккаунта: " + account.getBalance());
            System.out.println();
        }
    }

    public void printCategories() {
        List<Category> categories = operationService.getCategoryService().getCategories();
        for (Category category : categories) {
            System.out.println("ID категории: " + category.getId());
            System.out.println("Название категории: " + category.getName());
            System.out.println("Тип категории: " + category.getType());
            System.out.println();
        }
    }

    public void printOperations() {
        List<Operation> operations = operationService.getOperations();
        for (Operation operation : operations) {
            System.out.println("ID операции: " + operation.getId());
            System.out.println("Сумма операции: " + operation.getAmount());
            System.out.println("Описание операции: " + operation.getDescription());
            System.out.println("ID категории операции: " + operation.getCategory_id());
            System.out.println("ID банковского аккаунта операции: " + operation.getBank_account_id());
            System.out.println();
        }
    }

    public void countAllMoney() {
        List<BankAccount> accounts = operationService.getBankAccountService().getAccounts();
        int sum = 0;
        for (BankAccount account : accounts) {
            sum += account.getBalance();
        }
        System.out.println("Общий баланс: " + sum);
    }

    public Report buildReport() {
        return reportOperationObserver.buildReport();
    }

    private void saveData(ReportFormat format) {
        File dir = new File("./record");
        if (!dir.exists()) {
            dir.mkdirs();
        }    

        IDataExporter exporter = dataExporterFactory.saveData(format);
        try (Writer writer = new FileWriter("./record/data." + format)) {
            exporter.export(operationService.getBankAccountService().getAccounts(), operationService.getCategoryService().getCategories(), operationService.getOperations(), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadData(ReportFormat format) {
        IDataImporter importer = dataImporterFactory.loadData(format);
        File file = new File("./record/data." + format);
        if (!file.exists()) {
            System.out.println("Файл данных не найден: " + file.getPath());
            return;
        }
        try (Reader reader = new FileReader(file)) {
            importer.importData(operationService, reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyObserverForOperation(BankAccount account, Category category, Operation operation, OperationType type) {
        reportOperationObserver.onOperation(account, category, operation, type);
    }
}