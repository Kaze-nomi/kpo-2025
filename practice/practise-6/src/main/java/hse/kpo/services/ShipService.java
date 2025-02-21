package hse.kpo.services;

import hse.kpo.domains.Ship;
import hse.kpo.domains.Customer;
import hse.kpo.interfaces.IShipFactory;
import hse.kpo.interfaces.IShipProvider;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ShipService implements IShipProvider {

    private final List<Ship> ships = new ArrayList<>();

    private int shipNumberCounter = 0;

    @Override
    public Ship takeShip(Customer customer) {
        

        var filteredShips = ships.stream().filter(ship -> ship.isCompatible(customer)).toList();

        var firstShip = filteredShips.stream().findFirst();

        firstShip.ifPresent(ships::remove);

        return firstShip.orElse(null);
    }

    @Override
    public <TParams> void addShip(IShipFactory<TParams> shipFactory, TParams shipParams)
    {
        // создаем корабль из переданной фабрики
        var ship = shipFactory.createShip(
                shipParams, // передаем параметры
                ++shipNumberCounter // передаем номер - номер будет начинаться с 1
        );
        
        if (ship == null) {
            log.error("Failed to create ship");
            return;
        } 

        ships.add(ship); // добавляем корабль
    }
}

