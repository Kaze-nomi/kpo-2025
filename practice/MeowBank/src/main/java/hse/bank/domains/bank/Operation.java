package hse.bank.domains.bank;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Operation {
    @Getter
    private int id;

    // true - доход, false - расход
    @Getter
    @Setter
    private Boolean type;

    @Getter
    private int bank_account_id;

    @Getter
    private double amount;

    @Getter
    private Date date;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private int category_id;

    public Operation(int id, Boolean type, int bank_account_id, double amount, Date date, String description, int category_id) {
        this.id = id;
        this.type = type;
        this.bank_account_id = bank_account_id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category_id = category_id;
    }
}