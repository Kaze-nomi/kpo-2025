package hse.kpo.services;

import java.util.List;
import java.util.Optional;

import hse.kpo.domains.customers.Customer;
import hse.kpo.dto.CustomerRequest;
import hse.kpo.exception.KpoException;
import hse.kpo.interfaces.providerInterfaces.ICustomerProvider;
import hse.kpo.kafka.KafkaProducerService;
import hse.kpo.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerService implements ICustomerProvider {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(int id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer addCustomer(Customer customer) {
        var saved_customer = customerRepository.save(customer);
        kafkaProducerService.sendCustomerToTraining(saved_customer);
        return saved_customer;
    }

    @Transactional
    @Override
    public Customer updateCustomer(CustomerRequest request) {
        var customerOptional = customerRepository.findByName(request.name());

        if (customerOptional != null) {
            customerOptional.setIq(request.iq());
            customerOptional.setHandPower(request.handPower());
            customerOptional.setLegPower(request.legPower());
            return customerRepository.save(customerOptional);
        }
        throw new KpoException(HttpStatus.NOT_FOUND.value(), String.format("no customer with name: %s", request.name()));
    }

    @Transactional
    @Override
    public boolean deleteCustomer(String name) {
        customerRepository.deleteByName(name); // Добавьте метод в CustomerRepository
        return true;
    }
}