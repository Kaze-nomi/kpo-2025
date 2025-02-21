package hse.kpo.interfaces;

import hse.kpo.domains.Ship;

public interface IShipFactory<TParams> {
    Ship createShip(TParams shipParams, int shipNumber);
}