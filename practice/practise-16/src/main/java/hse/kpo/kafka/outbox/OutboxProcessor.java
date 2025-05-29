package hse.kpo.kafka.outbox;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import hse.kpo.domains.customers.Customer;
import hse.kpo.kafka.KafkaProducerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxEventRepository repository;

    private final KafkaProducerService kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processOutboxEvents() {

        List<OutboxEvent> events = repository.findAllBySentFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {
            try {
                Customer customer = objectMapper.readValue(event.getPayload(), Customer.class);
                kafkaTemplate.sendCustomerToTraining(customer);
                event.setSent(true);
                repository.save(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
}