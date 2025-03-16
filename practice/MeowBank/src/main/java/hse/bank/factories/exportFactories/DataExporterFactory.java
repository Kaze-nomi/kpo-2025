package hse.bank.factories.exportFactories;

import org.springframework.stereotype.Component;

import hse.bank.exporters.CSVDataExporter;
import hse.bank.exporters.JsonDataExporter;
import hse.bank.exporters.YAMLDataExporter;
import hse.bank.interfaces.exporterInterfaces.IDataExporter;
import hse.bank.params.ReportFormat;

@Component
public class DataExporterFactory {
    public IDataExporter saveData(ReportFormat format) {
        return switch (format) {
            case JSON -> new JsonDataExporter();
            case YAML -> new YAMLDataExporter();
            case CSV -> new CSVDataExporter();
        };
    }
}
