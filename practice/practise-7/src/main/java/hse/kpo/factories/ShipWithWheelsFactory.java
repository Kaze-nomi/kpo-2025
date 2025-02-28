package hse.kpo.factories;


import hse.kpo.domains.Car;
import hse.kpo.domains.ShipWithWheels;
import hse.kpo.domains.PedalEngine;
import hse.kpo.interfaces.ICarFactory;
import hse.kpo.params.PedalEngineParams;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShipWithWheelsFactory implements ICarFactory<PedalEngineParams> {
    @Override
    public ShipWithWheels createCar(PedalEngineParams carParams, int carNumber) {
        if (carParams.pedalSize() <= 0) {
            log.error("Pedal size must be greater than 0");
            return null;
        }
        var engine = new PedalEngine(carParams.pedalSize()); // создаем двигатель на основе переданных параметров

        return new ShipWithWheels(carNumber, engine); // возвращаем собранный катамаран с колёсиками с установленным двигателем и определенным номером
    }
}