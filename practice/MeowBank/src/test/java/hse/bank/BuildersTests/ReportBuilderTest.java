package hse.bank.BuildersTests;

import hse.bank.builders.ReportBuilder;
import hse.bank.builders.Report;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportBuilderTest {
	@Test
	@DisplayName("Проверка всех методов ReportBuilder и Report")
	void ReportBuilderCheck() {
		ReportBuilder reportBuilder = new ReportBuilder();
		ReportBuilder reportBuilder2 = new ReportBuilder();
		
		reportBuilder.addOperation("Operation1");
		reportBuilder.addOperation("Operation2");
		reportBuilder.addOperation("Operation3");
		
		Report report1 = reportBuilder.build();
		Report report2 = reportBuilder2.build();
		
		assertNotEquals(report1, report2);

		Report report3 = reportBuilder2.addOperation("Operation4").build();

		report3.toString();

		assertTrue(report3.toString().contains("Operation4"));
	}
}