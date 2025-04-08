package hse.bank.factories.bankFactories;

import java.util.Date;

import org.springframework.stereotype.Component;

import hse.bank.domains.bank.Operation;

@Component
public class OperationFactory {
    public Operation createOperation(int id, Boolean type, int bank_account_id, double amount, Date date, String description, int category_id) {
        return new Operation(id, type, bank_account_id, amount, date, description, category_id);
    }
}