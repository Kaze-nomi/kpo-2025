package hse.bank.domains.bank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Category {
    @Getter
    private int id;

    // true - доход, false - расход
    @Getter
    @Setter
    private Boolean type;

    @Getter
    @Setter
    private String name;

    public Category(int id, Boolean type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
}