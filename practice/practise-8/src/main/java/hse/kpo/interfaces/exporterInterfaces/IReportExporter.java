package hse.kpo.interfaces.exporterInterfaces;

import hse.kpo.builders.Report;
import java.io.IOException;
import java.io.Writer;

public interface IReportExporter {
    void export(Report report, Writer writer) throws IOException;
}