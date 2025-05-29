package hse.kpo.exporters;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.kpo.builders.Report;
import hse.kpo.interfaces.exporterInterfaces.IReportExporter;

import java.io.IOException;
import java.io.Writer;

public class JsonReportExporter implements IReportExporter {
private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void export(Report report, Writer writer) throws IOException {
        objectMapper.writeValue(writer, report);
    }
}