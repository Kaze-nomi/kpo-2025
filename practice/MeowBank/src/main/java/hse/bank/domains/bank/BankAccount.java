package hse.bank.domains.bank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class BankAccount {
    @Getter
    private int id;
    
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private double balance;

    public BankAccount(int id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }
}