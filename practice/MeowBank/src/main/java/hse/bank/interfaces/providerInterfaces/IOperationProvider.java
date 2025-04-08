package hse.bank.interfaces.providerInterfaces;

import java.util.Date;
import java.util.List;

import hse.bank.domains.bank.Operation;

public interface IOperationProvider {
    public List<Operation> getOperations();
    public IBankAccountProvider getBankAccountService();
    public ICategoryProvider getCategoryService();
    public Operation getOperation(int id);
    public Operation addOperation(int bank_account_id, double amount, Date date, String description, Boolean type, int category_id);
    public Operation editOperationDescription(int id, String description);
    public Operation editOperationCategory (int id, int category_id);
    public void deleteOperation(int id);
    public void deleteOperationsByBankAccountId(int id);
    public Boolean checkOperation(int cat_id);
    public void addOperationById(int id, int bank_account_id, double amount, Date date, String description, Boolean type, int category_id);
}