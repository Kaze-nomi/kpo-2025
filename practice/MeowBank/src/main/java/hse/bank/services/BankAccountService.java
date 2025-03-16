package hse.bank.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import hse.bank.domains.bank.BankAccount;
import hse.bank.factories.bankFactories.BankAccountFactory;
import hse.bank.interfaces.providerInterfaces.IBankAccountProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BankAccountService implements IBankAccountProvider {

    @Getter
    private final BankAccountFactory bankAccountFactory;

    @Getter
    private final List<BankAccount> accounts = new ArrayList<>();

    private int accountNumberCounter = 0;

    @Override
    public BankAccount addAccount(String name) {
        while (checkAccount(accountNumberCounter)) {
            accountNumberCounter++;
        }
        var account = bankAccountFactory.createBankAccount(accountNumberCounter++, name, 0.0);
        accounts.add(account);
        return account;
    }

    @Override
    public BankAccount editAccountName(int id, String name) {
        var account = accounts.stream().filter(a -> a.getId() == id).findFirst();
        if (account.isPresent()) {
            account.get().setName(name);
            return account.get();
        } else {
            System.out.println("Bank account with id %d does not exists".formatted(id));
            return null;
        }
    }

    @Override
    public BankAccount editAccountBalance(int id, double add_amount) {
        var account = accounts.stream().filter(a -> a.getId() == id).findFirst();
        if (account.isPresent()) {
            account.get().setBalance(account.get().getBalance() + add_amount);
            return account.get();
        } else {
            System.out.println("Bank account with id %d does not exists".formatted(id));
            return null;
        }
    }

    @Override
    public void deleteAccount(int id) {
        var account = accounts.stream().filter(a -> a.getId() == id).findFirst();
        if (account.isPresent()) {
            accounts.remove(account.get());
        } else {
            System.out.println("Bank account with id %d does not exists".formatted(id));
        }
    }

    @Override
    public Boolean checkAccount(int id) {
        var account = accounts.stream().filter(a -> a.getId() == id).findFirst();
        if (account.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BankAccount getAccount(int id) {
        var account = accounts.stream().filter(a -> a.getId() == id).findFirst();
        if (account.isPresent()) {
            return account.get();
        } else {
            System.out.println("Bank account with id %d does not exists".formatted(id));
            return null;
        }
    }

    @Override
    public void addAccountById(int id, String name, double balance) {
        var account = bankAccountFactory.createBankAccount(id, name, balance);
        accounts.add(account);
    }
}