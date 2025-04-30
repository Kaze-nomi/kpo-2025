package hse.kpo.factories.shipFactories;


import hse.kpo.domains.engines.PedalEngine;
import hse.kpo.domains.ships.Ship;
import hse.kpo.interfaces.factoryInterfaces.IShipFactory;
import hse.kpo.params.PedalEngineParams;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PedalShipFactory implements IShipFactory<PedalEngineParams> {
    @Override
    public Ship createShip(PedalEngineParams shipParams) {
        if (shipParams.pedalSize() <= 0) {
            log.error("Pedal size must be greater than 0");
            return null;
        }
        var engine = new PedalEngine(shipParams.pedalSize()); // создаем двигатель на основе переданных параметров

        return new Ship(engine); // возвращаем собранный корабль с установленным двигателем и определенным номером
    }
}
