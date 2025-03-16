package hse.bank.interfaces.observerInterfaces;


import hse.bank.domains.bank.BankAccount;
import hse.bank.domains.bank.Operation;
import hse.bank.params.OperationType;
import hse.bank.domains.bank.Category;

public interface IOperationObserver {
    void onOperation(BankAccount account, Category category, Operation operation, OperationType type);
}