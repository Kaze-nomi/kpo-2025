package hse.kpo.exporters;

import hse.kpo.builders.Report;
import hse.kpo.interfaces.exporterInterfaces.IReportExporter;

import java.io.IOException;
import java.io.Writer;

public class MarkdownReportExporter implements IReportExporter {
    @Override
    public void export(Report report, Writer writer) throws IOException {
        writer.write("# " + report.title() + "\n\n");
        String[] lines = report.content().split("\n");
        for (String line : lines) {
            if (line.startsWith("Операция: ")) {
                writer.write("## " + line.substring(10) + "\n");
            } else {
                writer.write(line + "\n");
            }
        }
        writer.flush();
    }
}