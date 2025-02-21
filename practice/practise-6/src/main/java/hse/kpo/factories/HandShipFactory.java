package hse.kpo.factories;

import hse.kpo.domains.Ship;
import hse.kpo.domains.HandEngine;
import hse.kpo.interfaces.IShipFactory;
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