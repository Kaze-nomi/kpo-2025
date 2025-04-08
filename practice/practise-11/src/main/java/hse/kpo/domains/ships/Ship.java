package hse.kpo.domains.ships;

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
@Table(name = "ships")
@ToString
@NoArgsConstructor
public class Ship implements ITransport {

    @Getter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "engine_id")
    private AbstractEngine engine;

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int VIN;

    @Override
    public String getTransportType() {
        return "Ship";
    }

    @Override
    public String getEngineType() {
        return engine.toString();
    }

    @Override
    public int getID() {
        return VIN;
    }

    public Ship(AbstractEngine engine) {
        this.engine = engine;
    }

    public Ship(int VIN, AbstractEngine engine) {
        this.VIN = VIN;
        this.engine = engine;
    }

    @Override
    public boolean isCompatible(Customer customer) {
        return this.engine.isCompatible(customer, ProductionTypes.CATAMARAN); // внутри метода просто вызываем соответствующий метод двигателя
    }
}