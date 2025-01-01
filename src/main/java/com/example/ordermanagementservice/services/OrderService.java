package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.clients.InventoryServiceClient;
import com.example.ordermanagementservice.dtos.InventoryReservationRequestDto;
import com.example.ordermanagementservice.dtos.InventoryReservationResponseDto;
import com.example.ordermanagementservice.dtos.RevokeInventoryReservationDto;
import com.example.ordermanagementservice.models.*;
import com.example.ordermanagementservice.repositories.OrderItemRepository;
import com.example.ordermanagementservice.repositories.OrderRepository;
import com.example.ordermanagementservice.repositories.OrderTrackingRepository;
import com.example.ordermanagementservice.utils.IDtoMapper;
import com.example.ordermanagementservice.utils.IdGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class OrderService implements IOrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderTrackingRepository orderTrackingRepository;

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
                log.info("Reserved Item: {} Quantity: {}", item.getProductId(), item.getQuantity());
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
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Order savedOrder = null;
        try {
            savedOrder = saveOrder(order);
        } catch (DataAccessException e) {
            log.error("Error while saving order {} of user {}", orderId, order.getUserId());
            handleInventoryRevocation(order);
        }

        return savedOrder;
    }

    @Transactional
    protected Order saveOrder(Order order) {
        orderItemRepository.saveAll(order.getOrderItems());
        orderTrackingRepository.save(order.getOrderTracking());
        return orderRepository.save(order);
    }

    private void handleInventoryRevocation(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            RevokeInventoryReservationDto revokeInventoryReservationDto = new RevokeInventoryReservationDto();
            revokeInventoryReservationDto.setRevokeType("cancelled");
            revokeInventoryReservationDto.setOrderId(order.getOrderId());
            revokeInventoryReservationDto.setReservationId(item.getReservationId());

            // Call the Inventory service to revoke the reservation
            inventoryServiceClient.revokeReservation(revokeInventoryReservationDto);
            log.info("Revoked reservation for item {}", item.getProductId());
        }
    }
}
