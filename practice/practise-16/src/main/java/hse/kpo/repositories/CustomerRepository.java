package hse.kpo.repositories;

import hse.kpo.domains.customers.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("""
        DELETE FROM Customer c
        WHERE c.name = :name
    """)
    void deleteByName(String name);
    Customer findByName(String name);
}