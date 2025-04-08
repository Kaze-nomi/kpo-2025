package hse.kpo.domains.cars;

import hse.kpo.domains.customers.Customer;
import hse.kpo.interfaces.domainInterfaces.ITransport;
import hse.kpo.interfaces.engineInterfaces.AbstractEngine;
import hse.kpo.params.ProductionTypes;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "cars")
@ToString
@NoArgsConstructor
public class Car implements ITransport {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int VIN;

    
    @Getter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "engine_id")
    private AbstractEngine engine;
    
    public Car(int VIN, AbstractEngine engine) {
        this.VIN = VIN;
        this.engine = engine;
    }

    @Override
    public int getID() {
        return VIN;
    }
    
    public Car(AbstractEngine engine) {
        this.engine = engine;
    }

    @Override
    public boolean isCompatible(Customer customer) {
        return engine.isCompatible(customer, ProductionTypes.CAR);
    }

    @Override
    public String getEngineType() {
        return engine.toString();
    }

    @Override
    public String getTransportType() {
        return "Car";
    }

}