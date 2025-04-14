package hse.kpo.services;

import hse.kpo.domains.customers.Customer;
import hse.kpo.domains.ships.Ship;
import hse.kpo.interfaces.factoryInterfaces.IShipFactory;
import hse.kpo.interfaces.providerInterfaces.IShipProvider;
import hse.kpo.repositories.ShipRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipService implements IShipProvider {

    private final ShipRepository shipRepository;

    @Override
    public Ship takeShip(Customer customer) {
        

        var filteredShips = shipRepository.findAll().stream().filter(ship -> ship.isCompatible(customer)).toList();

        var firstShip = filteredShips.stream().findFirst();

        firstShip.ifPresent(shipRepository::delete);

        return firstShip.orElse(null);
    }
    
    public <T> Ship addShip(IShipFactory<T> shipFactory, T shipParams) {
        return shipRepository.save(shipFactory.createShip(shipParams));
    }


    public Ship addExistingShip(Ship ship) {
        return shipRepository.save(ship);
    }

    public void deleteShip(int VIN) {
        shipRepository.deleteById(VIN);
    }


    public List<Ship> getShips() {
        return shipRepository.findAll();
    }

}