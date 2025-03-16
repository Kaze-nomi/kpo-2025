package hse.bank.interfaces.providerInterfaces;

import java.util.List;

import hse.bank.domains.bank.BankAccount;

public interface IBankAccountProvider {
    public List<BankAccount> getAccounts();
    public BankAccount getAccount(int id);
    public BankAccount addAccount(String name);
    public BankAccount editAccountName(int id, String name);
    public BankAccount editAccountBalance(int id, double balance);
    public void deleteAccount(int id);
    public Boolean checkAccount(int id);
    public void addAccountById(int id, String name, double balance);
}