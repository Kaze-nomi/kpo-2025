package hse.kpo.exporters;

import hse.kpo.builders.Report;
import hse.kpo.interfaces.exporterInterfaces.IReportExporter;

import java.io.IOException;
import java.io.Writer;

public class MarkdownReportExporter implements IReportExporter {
    @Override
    public void export(Report report, Writer writer) throws IOException {
        writer.write("# " + report.title() + "\n\n");
        writer.write(report.content());
        writer.flush();
    }
}