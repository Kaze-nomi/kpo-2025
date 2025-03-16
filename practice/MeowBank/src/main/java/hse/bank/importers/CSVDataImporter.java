package hse.bank.importers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hse.bank.interfaces.importerInterfaces.IDataImporter;
import hse.bank.interfaces.providerInterfaces.IOperationProvider;

public class CSVDataImporter implements IDataImporter {

    @Override
    public void importData(IOperationProvider operationService, Reader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            String currentSection = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("id,name,balance")) {
                    currentSection = "accounts";
                } else if (line.startsWith("id,name,type")) {
                    currentSection = "categories";
                } else if (line.startsWith("id,type,bank_account_id,amount,date,description,category_id")) {
                    currentSection = "operations";
                } else {
                    if (currentSection == null) continue;

                    List<String> data = Arrays.asList(line.split(","));
                    if (data.isEmpty()) continue;

                    try {
                        switch (currentSection) {
                            case "accounts":
                                if (data.size() != 3) break;
                                int accountId = Integer.parseInt(data.get(0));
                                String accountName = data.get(1);
                                double balance = Double.parseDouble(data.get(2));
                                operationService.getBankAccountService().addAccountById(accountId, accountName, balance);
                                break;
                            case "categories":
                                if (data.size() != 3) break;
                                int categoryId = Integer.parseInt(data.get(0));
                                String categoryName = data.get(1);
                                boolean type = Boolean.parseBoolean(data.get(2));
                                operationService.getCategoryService().addCategoryById(categoryId, categoryName, type);
                                break;
                            case "operations":
                                if (data.size() != 7) break;
                                int operationId = Integer.parseInt(data.get(0));
                                boolean opType = Boolean.parseBoolean(data.get(1));
                                int bankAccountId = Integer.parseInt(data.get(2));
                                double amount = Double.parseDouble(data.get(3));
                                Date date = parseDate(data.get(4));
                                String description = data.get(5);
                                int catId = Integer.parseInt(data.get(6));
                                operationService.addOperationById(operationId, bankAccountId, amount, date, description, opType, catId);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse date: " + dateStr, e);
        }
    }
}