package hse.kpo.service;

import hse.kpo.domains.Customer;
import hse.kpo.kafka.CustomerAddedEvent;
import hse.kpo.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TrainingConsumer {

    @Autowired
    private CustomerRepository customerRepository;

    @KafkaListener(topics = "customers", groupId = "kpo")
    @Transactional
    public void handleCustomerEvent(CustomerAddedEvent event) {
        Customer customer = new Customer(
            event.customerId(),
            event.customerName(),
            event.handPower(),
            event.legPower(),
            event.iq()
        );
        customerRepository.save(customer);
    }
}