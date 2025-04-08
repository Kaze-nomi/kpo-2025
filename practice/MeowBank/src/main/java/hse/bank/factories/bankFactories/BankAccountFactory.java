package hse.bank.factories.bankFactories;

import hse.bank.domains.bank.BankAccount;

import org.springframework.stereotype.Component;

@Component
public class BankAccountFactory {
    public BankAccount createBankAccount(int id, String name, double balance) {
        return new BankAccount(id, name, balance);
    }
}