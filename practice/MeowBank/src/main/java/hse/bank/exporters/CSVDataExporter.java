package hse.bank.exporters;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import hse.bank.domains.bank.BankAccount;
import hse.bank.domains.bank.Category;
import hse.bank.domains.bank.Operation;
import hse.bank.interfaces.exporterInterfaces.IDataExporter;

public class CSVDataExporter implements IDataExporter {

    @Override
    public void export(List<BankAccount> accounts, List<Category> categories, List<Operation> operations, Writer writer) throws IOException {
        writer.write("id,name,balance\n");
        for (BankAccount account : accounts) {
            writer.write("%d,%s,%f\n".formatted(account.getId(), account.getName(), account.getBalance()));
        }
        writer.write("id,name,type\n");
        for (Category category : categories) {
            writer.write("%d,%s,%b\n".formatted(category.getId(), category.getName(), category.getType()));
        }
        writer.write("id,type,bank_account_id,amount,date,description,category_id\n");
        for (Operation operation : operations) {
            writer.write("%d,%b,%d,%f,%s,%s,%d\n".formatted(operation.getId(), operation.getType(), operation.getBank_account_id(), operation.getAmount(), operation.getDate(), operation.getDescription(), operation.getCategory_id()));
        }
    }
}