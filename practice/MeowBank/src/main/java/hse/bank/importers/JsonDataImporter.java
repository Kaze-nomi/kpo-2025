package hse.bank.importers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import hse.bank.interfaces.importerInterfaces.IDataImporter;
import hse.bank.interfaces.providerInterfaces.IOperationProvider;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JsonDataImporter implements IDataImporter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void importData(IOperationProvider operationService, Reader reader) throws IOException {
        Map<String, List<Map<String, Object>>> data = objectMapper.readValue(reader, new TypeReference<Map<String, List<Map<String, Object>>>>() {});

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
            Date date = new Date();
            date.setTime((long)op.get("date"));            
            String description = (String) op.get("description");
            int categoryId = ((Number) op.get("category_id")).intValue();
            service.addOperationById(operationId, accountId, amount, date, description, type, categoryId);
        }
    }
}