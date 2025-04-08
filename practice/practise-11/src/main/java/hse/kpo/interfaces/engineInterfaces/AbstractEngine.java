package hse.kpo.interfaces.engineInterfaces;

import hse.kpo.domains.customers.Customer;
import hse.kpo.params.ProductionTypes;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "engine_type")
public class AbstractEngine implements IEngine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "engine_type", insertable = false, updatable = false)
    private String type; // Автоматически заполняется дискриминатором
    

    @Override
    public boolean isCompatible(Customer customer, ProductionTypes type) {
        return false;
    }
}