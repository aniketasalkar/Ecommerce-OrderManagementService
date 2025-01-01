package com.example.ordermanagementservice.repositories;

import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order save(Order order);
}
