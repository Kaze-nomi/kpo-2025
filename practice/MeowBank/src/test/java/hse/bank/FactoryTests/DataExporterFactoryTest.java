package hse.bank.FactoryTests;

import hse.bank.factories.exportFactories.DataExporterFactory;
import hse.bank.interfaces.exporterInterfaces.IDataExporter;
import hse.bank.params.ReportFormat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataExporterFactoryTest {
	@Test
	@DisplayName("Проверка всех методов DataExporterFactory")
	void DataExporterFactoryCheck() {
        DataExporterFactory factory = new DataExporterFactory();
        
        IDataExporter exporter = factory.saveData(ReportFormat.CSV);

        IDataExporter exporter2 = factory.saveData(ReportFormat.JSON);

        IDataExporter exporter3 = factory.saveData(ReportFormat.YAML);

        assertNotNull(exporter);

        assertNotNull(exporter2);

        assertNotNull(exporter3);
	}
}

