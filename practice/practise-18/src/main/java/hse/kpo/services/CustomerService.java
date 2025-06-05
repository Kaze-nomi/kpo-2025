package hse.kpo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import hse.kpo.domains.customers.Customer;
import hse.kpo.dto.CustomerRequest;
import hse.kpo.exception.KpoException;
import hse.kpo.interfaces.providerInterfaces.ICustomerProvider;
import hse.kpo.kafka.outbox.OutboxEvent;
import hse.kpo.kafka.outbox.OutboxEventRepository;
import hse.kpo.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Service
public class CustomerService implements ICustomerProvider {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private final OutboxEventRepository outboxEventRepository;

    @Autowired
    private final ObjectMapper objectMapper;

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
        saveToOutbox(saved_customer);
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
        customerRepository.deleteByName(name);
        return true;
    }

    private void saveToOutbox(Customer customer) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType("Customer");
        outboxEvent.setEventType("CustomerAdded");
        outboxEvent.setCreatedAt(LocalDateTime.now());

        try {
            outboxEvent.setPayload(objectMapper.writeValueAsString(customer));
        } catch (JsonProcessingException e) {
            throw new KpoException(500, "Failed to serialize customer event");
        }

        outboxEventRepository.save(outboxEvent);
    }

}