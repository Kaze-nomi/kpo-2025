package hse.bank.exporters;

import org.yaml.snakeyaml.Yaml;
import hse.bank.domains.bank.BankAccount;
import hse.bank.domains.bank.Category;
import hse.bank.domains.bank.Operation;
import hse.bank.interfaces.exporterInterfaces.IDataExporter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YAMLDataExporter implements IDataExporter {

    @Override
    public void export(List<BankAccount> accounts, List<Category> categories, List<Operation> operations, Writer writer) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();
        
        data.put("accounts", convertAccounts(accounts));
        data.put("categories", convertCategories(categories));
        data.put("operations", convertOperations(operations));
        
        yaml.dump(data, writer);
    }

    private List<Map<String, Object>> convertAccounts(List<BankAccount> accounts) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (BankAccount acc : accounts) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", acc.getId());
            map.put("name", acc.getName());
            map.put("balance", acc.getBalance());
            result.add(map);
        }
        return result;
    }

    private List<Map<String, Object>> convertCategories(List<Category> categories) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category cat : categories) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", cat.getId());
            map.put("name", cat.getName());
            map.put("type", cat.getType());
            result.add(map);
        }
        return result;
    }

    private List<Map<String, Object>> convertOperations(List<Operation> operations) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Operation op : operations) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", op.getId());
            map.put("type", op.getType());
            map.put("bank_account_id", op.getBank_account_id());
            map.put("amount", op.getAmount());
            map.put("date", op.getDate());
            map.put("description", op.getDescription());
            map.put("category_id", op.getCategory_id());
            result.add(map);
        }
        return result;
    }
}