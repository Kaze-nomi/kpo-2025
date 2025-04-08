package hse.kpo.repositories;

import hse.kpo.domains.cars.Car;
import hse.kpo.domains.ships.Ship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ShipRepository extends JpaRepository<Ship, Integer> {
    @Query("""
        SELECT c 
        FROM Ship c 
        JOIN c.engine e 
        WHERE e.type = :engineType 
        AND c.VIN > :minVin
    """)
    List<Car> findShipByEngineTypeAndVinGreaterThan(
            @Param("engineType") String engineType,
            @Param("minVin") Integer minVin
    );
}