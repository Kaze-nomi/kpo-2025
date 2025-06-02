package hse.orders.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hse.orders.domains.Order;

public interface OrdersRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByUserId(Integer userId);

}