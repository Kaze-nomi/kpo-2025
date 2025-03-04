package hse.kpo.exporters;

import hse.kpo.interfaces.domainInterfaces.ITransport;
import hse.kpo.interfaces.exporterInterfaces.ITransportExporter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class XMLReportExporter implements ITransportExporter {

    @Override
    public void export(List<ITransport> transports, Writer writer) throws IOException {
        writer.write("<Transport>\n");
        for (ITransport transport : transports) {
            writer.write(String.format("""
                <Vehicle>
                    <VIN>%d</VIN>
                    <Type>%s</Type>
                    <Engine>
                        <Type>%s</Type>
                    </Engine>
                </Vehicle>
                """,
                transport.getID(),
                transport.getTransportType(),
                transport.getEngineType()
                ));
        }
        writer.write("</Transport>\n");
    }
}
