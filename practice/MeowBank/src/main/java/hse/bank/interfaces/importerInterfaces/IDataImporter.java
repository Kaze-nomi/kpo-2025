package hse.bank.interfaces.importerInterfaces;

import java.io.IOException;
import java.io.Reader;

import hse.bank.interfaces.providerInterfaces.IOperationProvider;

public interface IDataImporter {
    void importData(IOperationProvider operationService, Reader reader) throws IOException;
}