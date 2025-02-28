package hse.kpo.domains;
import hse.kpo.interfaces.IEngine;
import hse.kpo.params.ProductionTypes;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents a levitation engine.
 */
@ToString
@Getter
@Component
@RequiredArgsConstructor
public class LevitationEngine implements IEngine {

    /**
     * Checks if the engine is compatible with the customer.
     * 
     * @param customer the customer to check
     * @return true if the customer's IQ is 300 or higher, false otherwise
     */
    @Override
    public boolean isCompatible(Customer customer, ProductionTypes type) {
        return switch (type) {
            case ProductionTypes.CAR -> customer.getIq() >= 300;
            case ProductionTypes.CATAMARAN -> customer.getIq() >= 200;
            case null, default -> throw new RuntimeException("This type of production doesn't exist");
        };
    }
}


