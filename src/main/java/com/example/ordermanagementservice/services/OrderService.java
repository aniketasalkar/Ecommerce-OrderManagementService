package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.clients.InventoryServiceClient;
import com.example.ordermanagementservice.clients.UserAuthServiceClient;
import com.example.ordermanagementservice.clients.UserManagementServiceClient;
import com.example.ordermanagementservice.dtos.*;
import com.example.ordermanagementservice.exceptions.InvalidUserException;
import com.example.ordermanagementservice.exceptions.TokenExpiredException;
import com.example.ordermanagementservice.models.*;
import com.example.ordermanagementservice.repositories.OrderItemRepository;
import com.example.ordermanagementservice.repositories.OrderRepository;
import com.example.ordermanagementservice.repositories.OrderTrackingRepository;
import com.example.ordermanagementservice.utils.IdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    UserAuthServiceClient userAuthServiceClient;

    @Autowired
    UserManagementServiceClient userManagementServiceClient;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Order createOrder(Order order, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto) {

        validateUser(order.getUserId(), validateAndRefreshTokenRequestDto);
        String orderId = IdGenerator.generateId(14);

        List<OrderItem> filteredItems = order.getOrderItems().stream()
                .filter(item -> {
                    InventoryReservationRequestDto inventoryReservationRequestDto = new InventoryReservationRequestDto();
                    inventoryReservationRequestDto.setProductId(item.getProductId());
                    inventoryReservationRequestDto.setQuantity(item.getQuantity());
                    inventoryReservationRequestDto.setOrderId(orderId);

                    InventoryReservationResponseDto inventoryReservationResponseDto = inventoryServiceClient.
                            reserveInventoryItem(inventoryReservationRequestDto);

                    if (inventoryReservationResponseDto != null) {
                        item.setReservationId(inventoryReservationResponseDto.getReservationId());
                        log.info("Reserved Item: {} Quantity: {}", item.getProductId(), item.getQuantity());
                        return true; // Keep the item
                    }
                    return false; // Exclude the item
                })
                .collect(Collectors.toList());

        order.setOrderItems(filteredItems);

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
            try {
                totalAmount += item.getQuantity() * objectMapper.
                        readValue(item.getProductSnapshot(), ProductSnapshot.class).getPrice();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
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

    @Override
    public List<Order> getAllOrdersofUser(long userId, String filter, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto) {
        validateUser(userId, validateAndRefreshTokenRequestDto);
        List<Order> orders;
        switch (filter.toUpperCase().trim()) {
            case "CANCELLED":
                orders = orderRepository.findAllByUserIdAndOrderStatus(userId, OrderStatus.CANCELLED);
                break;
            case "ALL":
                orders = orderRepository.findAllByUserId(userId);
                break;
            default:
                orders = orderRepository.findOrderByUserIdAndOrderStatusIsNot(userId, OrderStatus.CANCELLED);

        }
        return orders;
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

    private void validateUser(long userId, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto) {
        UserResponseDto userResponseDto = userManagementServiceClient.getUserById(userId);

        if (userResponseDto.getId() != userId) {
            throw new InvalidUserException("Invalid user");
        }

        if (!userAuthServiceClient.validateToken(userResponseDto.getEmail(), validateAndRefreshTokenRequestDto)) {
            throw new TokenExpiredException("Token expired. Please try after login.");
        }
    }
}
