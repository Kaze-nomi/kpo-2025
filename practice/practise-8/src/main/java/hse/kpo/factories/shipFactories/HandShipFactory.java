package hse.kpo.factories.shipFactories;

import hse.kpo.domains.engines.HandEngine;
import hse.kpo.domains.ships.Ship;
import hse.kpo.interfaces.factoryInterfaces.IShipFactory;
import hse.kpo.params.EmptyEngineParams;
import org.springframework.stereotype.Component;

@Component
public class HandShipFactory implements IShipFactory<EmptyEngineParams> {
    @Override
    public Ship createShip(EmptyEngineParams shipParams, int shipNumber) {
        var engine = new HandEngine(); // Создаем двигатель без каких-либо параметров

        return new Ship(shipNumber, engine); // создаем корабль с ручным приводом
    }
}