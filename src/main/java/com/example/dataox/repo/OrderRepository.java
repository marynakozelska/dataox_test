package com.example.dataox.repo;

import com.example.dataox.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.supplier.id = :clientId OR o.consumer.id = :clientId")
    List<Order> findByClientId(@Param("clientId") Long clientId);
}