package hse.kpo.exporters;

import hse.kpo.interfaces.domainInterfaces.ITransport;
import hse.kpo.interfaces.exporterInterfaces.ITransportExporter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVReportExporter implements ITransportExporter {

    @Override
    public void export(List<ITransport> transports, Writer writer) throws IOException {
        for (ITransport transport : transports) {
            writer.write(String.format("%d,%s,%s\n",
                    transport.getID(),
                    transport.getTransportType(),
                    transport.getEngineType()));
        }
    }
}
