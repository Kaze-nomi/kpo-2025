package hse.kpo.interfaces.exporterInterfaces;

import hse.kpo.interfaces.domainInterfaces.ITransport;

import java.io.Writer;
import java.io.IOException;
import java.util.List;

public interface ITransportExporter {
    void export(List<ITransport> transports, Writer writer) throws IOException;
}