package hse.bank.interfaces.exporterInterfaces;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import hse.bank.domains.bank.BankAccount;
import hse.bank.domains.bank.Category;
import hse.bank.domains.bank.Operation;

public interface IDataExporter {
    void export(List<BankAccount> accounts, List<Category> categories, List<Operation> operations, Writer writer) throws IOException;
}