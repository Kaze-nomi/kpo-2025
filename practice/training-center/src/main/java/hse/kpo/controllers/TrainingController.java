package hse.kpo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hse.kpo.domains.Customer;
import hse.kpo.kafka.TrainingCompletedEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import hse.kpo.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Тренировочный центр", description = "Тренировка покупателей")
public class TrainingController {

        @Autowired
        private CustomerRepository customerStorage;

        @Autowired
        private KafkaTemplate<String, TrainingCompletedEvent> kafkaTemplate;

        @PutMapping("/train/{customerId}")
        @Operation(summary = "Тренировать покупателя", parameters = {
                        @Parameter(name = "trainingType", description = "Вид тренировки - handPower || legPower || iq"),
        })
        public ResponseEntity<String> trainCustomer(
                        @PathVariable Integer customerId,
                        @RequestParam String trainingType) {
                var customerOptional = customerStorage.findById(customerId);

                if (customerOptional.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Покупатель не найден");
                }

                var customer = customerOptional.get();
                switch (trainingType) {
                        case "handPower" -> customer.setHandPower(customer.getHandPower() + 1);
                        case "legPower" -> customer.setLegPower(customer.getLegPower() + 1);
                        case "iq" -> customer.setIq(customer.getIq() + 1);
                        default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Несуществующий тип тренировки");
                }

                customerStorage.save(customer);

                TrainingCompletedEvent event = new TrainingCompletedEvent(customerId, trainingType);
                kafkaTemplate.send("training-updates", String.valueOf(customerId), event);

                return ResponseEntity.ok("Тренировка выполнена");
        }

        @GetMapping
        @Operation(summary = "Получить всех покупателей")
        public List<Customer> getAllCustomers() {
                return customerStorage.findAll().stream().toList();
        }
}