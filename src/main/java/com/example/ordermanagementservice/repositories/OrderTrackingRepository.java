package com.example.ordermanagementservice.repositories;

import com.example.ordermanagementservice.models.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    OrderTracking save(OrderTracking orderTracking);
}
