package hse.kpo.factories;


import hse.kpo.domains.Ship;
import hse.kpo.domains.PedalEngine;
import hse.kpo.interfaces.IShipFactory;
import hse.kpo.params.PedalEngineParams;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PedalShipFactory implements IShipFactory<PedalEngineParams> {
    @Override
    public Ship createShip(PedalEngineParams shipParams, int shipNumber) {
        if (shipParams.pedalSize() <= 0) {
            log.error("Pedal size must be greater than 0");
            return null;
        }
        var engine = new PedalEngine(shipParams.pedalSize()); // создаем двигатель на основе переданных параметров

        return new Ship(shipNumber, engine); // возвращаем собранный корабль с установленным двигателем и определенным номером
    }
}
