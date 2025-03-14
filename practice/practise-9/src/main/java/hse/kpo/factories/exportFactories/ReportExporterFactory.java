package hse.kpo.factories.exportFactories;

import hse.kpo.params.ReportFormat;
import hse.kpo.exporters.JsonReportExporter;
import hse.kpo.exporters.MarkdownReportExporter;
import hse.kpo.exporters.XMLReportExporter;
import hse.kpo.exporters.CSVReportExporter;
import hse.kpo.interfaces.exporterInterfaces.IReportExporter;
import hse.kpo.interfaces.exporterInterfaces.ITransportExporter;

import org.springframework.stereotype.Component;

@Component
public class ReportExporterFactory {
    public IReportExporter createReport(ReportFormat format) {
        return switch (format) {
            case JSON -> new JsonReportExporter();
            case MARKDOWN -> new MarkdownReportExporter();
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    public ITransportExporter createTransoport(ReportFormat format) {
        return switch (format) {
            case XML -> new XMLReportExporter();
            case CSV -> new CSVReportExporter();
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }
}
