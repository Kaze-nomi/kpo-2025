package hse.kpo.interfaces.factoryInterfaces;

import hse.kpo.domains.ships.Ship;

public interface IShipFactory<TParams> {
    Ship createShip(TParams shipParams, int shipNumber);
}