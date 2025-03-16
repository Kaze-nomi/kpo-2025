package hse.bank.importers;

import org.yaml.snakeyaml.Yaml;

import hse.bank.interfaces.importerInterfaces.IDataImporter;
import hse.bank.interfaces.providerInterfaces.IOperationProvider;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class YAMLDataImporter implements IDataImporter {

    @Override
    public void importData(IOperationProvider operationService, Reader reader) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, List<Map<String, Object>>> data = yaml.load(reader);

        processAccounts(operationService, data.get("accounts"));
        processCategories(operationService, data.get("categories"));
        processOperations(operationService, data.get("operations"));
    }

    private void processAccounts(IOperationProvider service, List<Map<String, Object>> accounts) {
        if (accounts == null) return;
        for (Map<String, Object> acc : accounts) {
            int id = ((Number) acc.get("id")).intValue();
            String name = (String) acc.get("name");
            double balance = ((Number) acc.get("balance")).doubleValue();
            service.getBankAccountService().addAccountById(id, name, balance);
        }
    }

    private void processCategories(IOperationProvider service, List<Map<String, Object>> categories) {
        if (categories == null) return;
        for (Map<String, Object> cat : categories) {
            int id = ((Number) cat.get("id")).intValue();
            String name = (String) cat.get("name");
            boolean type = (Boolean) cat.get("type");
            service.getCategoryService().addCategoryById(id, name, type);
        }
    }

    private void processOperations(IOperationProvider service, List<Map<String, Object>> operations) {
        if (operations == null) return;
        for (Map<String, Object> op : operations) {
            int operationId = ((Number) op.get("id")).intValue();
            boolean type = (Boolean) op.get("type");
            int accountId = ((Number) op.get("bank_account_id")).intValue();
            double amount = ((Number) op.get("amount")).doubleValue();
            Date date = (Date)op.get("date");
            String description = (String) op.get("description");
            int categoryId = ((Number) op.get("category_id")).intValue();
            service.addOperationById(operationId, accountId, amount, date, description, type, categoryId);
        }
    }
}