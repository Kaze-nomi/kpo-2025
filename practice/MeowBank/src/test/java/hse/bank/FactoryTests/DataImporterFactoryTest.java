package hse.bank.FactoryTests;

import hse.bank.factories.importFactories.DataImporterFactory;
import hse.bank.interfaces.importerInterfaces.IDataImporter;
import hse.bank.params.ReportFormat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataImporterFactoryTest {
	@Test
	@DisplayName("Проверка всех методов DataImporterFactory")
	void DataImporterFactoryCheck() {
        DataImporterFactory factory = new DataImporterFactory();
        
        IDataImporter importer = factory.loadData(ReportFormat.CSV);

        IDataImporter importer2 = factory.loadData(ReportFormat.JSON);

        IDataImporter importer3 = factory.loadData(ReportFormat.YAML);

        assertNotNull(importer);

        assertNotNull(importer2);

        assertNotNull(importer3);
	}
}

