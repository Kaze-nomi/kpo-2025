package hse.bank.exporters;

import com.fasterxml.jackson.databind.ObjectMapper;

import hse.bank.domains.bank.BankAccount;
import hse.bank.domains.bank.Category;
import hse.bank.domains.bank.Operation;
import hse.bank.interfaces.exporterInterfaces.IDataExporter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class JsonDataExporter implements IDataExporter {
private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void export(List<BankAccount> accounts, List<Category> categories, List<Operation> operations, Writer writer) throws IOException {
        var data = Map.of("accounts", accounts, "categories", categories, "operations", operations);
        objectMapper.writeValue(writer, data);
    }
}
