package hse.bank.builders;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ReportBuilder {
    private StringBuilder content;

    public ReportBuilder() {
        this.content = new StringBuilder();
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