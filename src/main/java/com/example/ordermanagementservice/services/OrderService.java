package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.clients.InventoryServiceClient;
import com.example.ordermanagementservice.dtos.InventoryReservationRequestDto;
import com.example.ordermanagementservice.dtos.InventoryReservationResponseDto;
import com.example.ordermanagementservice.models.*;
import com.example.ordermanagementservice.repositories.OrderRepository;
import com.example.ordermanagementservice.utils.IDtoMapper;
import com.example.ordermanagementservice.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class OrderService implements IOrderService {

    @Autowired
    IDtoMapper dtoMapper;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    InventoryServiceClient inventoryServiceClient;

    @Override
    public Order createOrder(Order order) {

        String orderId = IdGenerator.generateId(14);

        for (OrderItem item : order.getOrderItems()) {
            InventoryReservationRequestDto inventoryReservationRequestDto = new InventoryReservationRequestDto();
            inventoryReservationRequestDto.setProductId(item.getProductId());
            inventoryReservationRequestDto.setQuantity(item.getQuantity());
            inventoryReservationRequestDto.setOrderId(orderId);

            InventoryReservationResponseDto  inventoryReservationResponseDto = inventoryServiceClient.
                    reserveInventoryItem(inventoryReservationRequestDto);

            if (inventoryReservationResponseDto != null) {
                item.setReservationId(inventoryReservationResponseDto.getReservationId());
            } else {
                order.getOrderItems().remove(item);
            }
        }

        Date now = new Date();

        OrderTracking orderTracking = new OrderTracking();
        orderTracking.setCurrentStatus(TrackingStatus.IN_TRANSIT);
        orderTracking.setCreatedAt(now);
        orderTracking.setUpdatedAt(now);
        order.setOrderTracking(orderTracking);

        double totalAmount = 0;
        for (OrderItem item : order.getOrderItems()) {
            item.setCreatedAt(now);
            item.setUpdatedAt(now);
            totalAmount += item.getQuantity() * item.getPrice();
        }

        if (totalAmount != order.getTotalAmount()) {
            order.setTotalAmount(totalAmount);
            log.info("Order Total adjusted to {}", totalAmount);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderDate(now);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderId(orderId);

        return orderRepository.save(order);
    }
}
