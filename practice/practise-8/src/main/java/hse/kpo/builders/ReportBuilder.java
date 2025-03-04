package hse.kpo.builders;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import hse.kpo.domains.customers.Customer;

public class ReportBuilder {
    private StringBuilder content;

    public ReportBuilder() {
        this.content = new StringBuilder();
    }

    public ReportBuilder addCustomers(List<Customer> customers) {
        content.append("Покупатели:");
        customers.forEach(customer -> content.append(String.format(" - %s", customer)));
        content.append("\n");

        return this;
    }

    public ReportBuilder addOperation(String operation) {
        content.append(String.format("Операция: %s", operation));
        content.append(System.lineSeparator());
        return this;
    }

    public Report build() {
        return new Report(String.format("Отчет за %s", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))),
                content.toString());
    }
}