package hse.kpo.factories.shipFactories;

import hse.kpo.domains.engines.LevitationEngine;
import hse.kpo.domains.ships.Ship;
import hse.kpo.interfaces.factoryInterfaces.IShipFactory;
import hse.kpo.params.EmptyEngineParams;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * A class that implements the IShipFactory interface and produces levitation ships
 */
@Component
@RequiredArgsConstructor
public class LevitationShipFactory implements IShipFactory<EmptyEngineParams> {
    /**
     * A method that creates a ship
     * @param shipNumber the number of the ship
     * @return the created ship
     */
    @Override
    public Ship createShip(EmptyEngineParams shipParams, int shipNumber) {
        var engine = new LevitationEngine();

        return new Ship(shipNumber, engine);
    }
}