package hse.kpo.interfaces.factoryInterfaces;

import hse.kpo.domains.ships.Ship;

public interface IShipFactory<T> {
    Ship createShip(T shipParams);
}