package hse.bank.factories.importFactories;

import org.springframework.stereotype.Component;

import hse.bank.importers.CSVDataImporter;
import hse.bank.importers.JsonDataImporter;
import hse.bank.importers.YAMLDataImporter;
import hse.bank.interfaces.importerInterfaces.IDataImporter;
import hse.bank.params.ReportFormat;

@Component
public class DataImporterFactory {
    public IDataImporter loadData(ReportFormat format) {
        return switch (format) {
            case JSON -> new JsonDataImporter();
            case YAML -> new YAMLDataImporter();
            case CSV -> new CSVDataImporter();
        };
    }
}
