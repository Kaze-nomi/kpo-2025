package hse.kpo.services;

import hse.kpo.domains.customers.Customer;
import hse.kpo.domains.ships.Ship;
import hse.kpo.interfaces.factoryInterfaces.IShipFactory;
import hse.kpo.interfaces.providerInterfaces.IShipProvider;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ShipService implements IShipProvider {

    @Getter
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
    public <TParams> Ship addShip(IShipFactory<TParams> shipFactory, TParams shipParams)
    {
        // создаем корабль из переданной фабрики
        var ship = shipFactory.createShip(
                shipParams, // передаем параметры
                ++shipNumberCounter // передаем номер - номер будет начинаться с 1
        );
        
        if (ship == null) {
            log.error("Failed to create ship");
            return ship;
        } 

        ships.add(ship); // добавляем корабль
        return ship;
    }

    @Override
    public void deleteShip(int VIN) {
        ships.removeIf(ship -> ship.getVIN() == VIN);
    }
}