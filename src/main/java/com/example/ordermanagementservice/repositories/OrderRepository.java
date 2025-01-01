package com.example.ordermanagementservice.repositories;

import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderItem;
import com.example.ordermanagementservice.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order save(Order order);
    List<Order> findAllByUserId(Long userId);
    List<Order> findAllByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);
    List<Order> findOrderByUserIdAndOrderStatusIsNot(Long userId, OrderStatus orderStatus);
//    Optional<Order> findByOrderId(Long orderId);
}
