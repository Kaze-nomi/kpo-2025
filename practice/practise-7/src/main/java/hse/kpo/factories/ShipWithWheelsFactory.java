package hse.kpo.factories;

import hse.kpo.domains.ShipWithWheels;
import hse.kpo.domains.Ship;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShipWithWheelsFactory {
    public ShipWithWheels createShipWithWheels(Ship ship) {
        return new ShipWithWheels(ship.getVIN() + 10000, ship.getEngine());
    }
}
