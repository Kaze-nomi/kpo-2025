package hse.kpo.repositories;

import hse.kpo.domains.cars.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
    @Query("""
        SELECT c 
        FROM Car c 
        JOIN c.engine e 
        WHERE e.type = :engineType 
        AND c.VIN > :minVin
    """)
    List<Car> findCarsByEngineTypeAndVinGreaterThan(
            @Param("engineType") String engineType,
            @Param("minVin") Integer minVin
    );
}