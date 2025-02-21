package hse.kpo.observers;

import hse.kpo.domains.Customer;
import hse.kpo.params.ProductionTypes;
import hse.kpo.builders.Report;
import hse.kpo.builders.ReportBuilder;
import hse.kpo.interfaces.ISalesObserver;
import hse.kpo.services.CustomerStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportSalesObserver implements ISalesObserver {
private final CustomerStorage customerStorage;

    private final ReportBuilder reportBuilder = new ReportBuilder();

    public Report buildReport() {
        return reportBuilder.build();
    }

    public void checkCustomers() {
        reportBuilder.addCustomers(customerStorage.getCustomers());
    }

    @Override
    public void onSale(Customer customer, ProductionTypes productType, int vin) {
        String message = String.format(
                "Продажа: %s VIN-%d клиенту %s (Сила рук: %d, Сила ног: %d, IQ: %d)",
                productType, vin, customer.getName(),
                customer.getHandPower(), customer.getLegPower(), customer.getIq()
        );
        reportBuilder.addOperation(message);
    }
}