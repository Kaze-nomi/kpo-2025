package hse.kpo.ServicesTests;

import hse.kpo.domains.customers.Customer;
import hse.kpo.services.CustomerStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KpoServicesTest_1 {

	@Autowired
	private CustomerStorage customerStorage;


	@Test
	@DisplayName("Проверка CutsomerStorage на содержание всех созданных покупателей")
	void customersCheck() {
        customerStorage.addCustomer(new Customer("John", 6, 4, 50));
        customerStorage.addCustomer(new Customer("Bob", 4, 6, 200));
        customerStorage.addCustomer(new Customer("Глеб", 0, 0, 300));
        customerStorage.addCustomer(new Customer("Jack", 4, 4, 2));

        List<Customer> customers = customerStorage.getCustomers();
        for (Customer customer : customers) {
            if (customer.getName().equals("John")) {
                assertEquals(6, customer.getLegPower());
                assertEquals(4, customer.getHandPower());
                assertEquals(50, customer.getIq());
            } else if (customer.getName().equals("Bob")) {
                assertEquals(4, customer.getLegPower());
                assertEquals(6, customer.getHandPower());
                assertEquals(200, customer.getIq());
            } else if (customer.getName().equals("Глеб")) {
                assertEquals(0, customer.getLegPower());
                assertEquals(0, customer.getHandPower());
                assertEquals(300, customer.getIq());
            } else if (customer.getName().equals("Jack")) {
                assertEquals(4, customer.getLegPower());
                assertEquals(4, customer.getHandPower());
                assertEquals(2, customer.getIq());
            }
        }
	}
}