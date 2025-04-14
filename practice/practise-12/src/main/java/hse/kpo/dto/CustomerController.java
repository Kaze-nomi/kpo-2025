package hse.kpo.dto;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hse.kpo.domains.customers.Customer;
import hse.kpo.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import hse.kpo.facade.HSE;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Покупатели", description = "Управление покупателями")
public class CustomerController {
    private final CustomerStorage customerStorage;
    private final HSE hse;

    @PostMapping
    @Operation(summary = "Добавить покупателя",
            description = "Добавляет покупателя в базу данных")
    public ResponseEntity<Customer> createCustomer(
            @Valid @RequestBody CustomerRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        String name = request.name();
        int legPower = request.legPower();
        int handPower = request.handPower();
        int iq = request.iq();
        if (name == null || name.isEmpty() || legPower <= 0 || handPower <= 0 || iq <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No this type");
        }

        var customer = hse.addCustomer(name, legPower, handPower, iq);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @GetMapping
    @Operation(summary = "Получить всех покупателей с фильтрацией",
            parameters = {
                    @Parameter(name = "legPower", description = "Фильтр по силе ног"),
                    @Parameter(name = "handPower", description = "Фильтр по силе рук"),
                    @Parameter(name = "iq", description = "Фильтр по IQ")
            })
    public List<Customer> getAllCustomers(
            @RequestParam(required = false) Integer legPower,
            @RequestParam(required = false) Integer handPower,
            @RequestParam(required = false) Integer iq) {

        return customerStorage.getCustomers().stream()
                .filter(customer -> legPower == null || customer.getLegPower() >= legPower)
                .filter(customer -> handPower == null || customer.getHandPower() >= handPower)
                .filter(customer -> iq == null || customer.getIq() >= iq)
                .toList();
    }

}