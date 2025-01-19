package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.clients.InventoryServiceClient;
import com.example.ordermanagementservice.clients.PaymentServiceClient;
import com.example.ordermanagementservice.clients.UserAuthServiceClient;
import com.example.ordermanagementservice.clients.UserManagementServiceClient;
import com.example.ordermanagementservice.dtos.*;
import com.example.ordermanagementservice.exceptions.*;
import com.example.ordermanagementservice.models.*;
import com.example.ordermanagementservice.repositories.OrderItemRepository;
import com.example.ordermanagementservice.repositories.OrderRepository;
import com.example.ordermanagementservice.repositories.OrderTrackingRepository;
import com.example.ordermanagementservice.utils.DtoMapper;
import com.example.ordermanagementservice.utils.IdGenerator;
import com.example.ordermanagementservice.utils.KafkaEventGenerator;
import com.example.ordermanagementservice.utils.TokenValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    PaymentServiceClient paymentServiceClient;

    @Autowired
    KafkaEventGenerator kafkaEventGenerator;

    @Autowired
    TokenValidation tokenValidation;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DtoMapper dtoMapper;

    private final long deliveryCancellationTimeInMillis = 7 * 24 * 60 * 60 * 1000;

    @Transactional
    @Override
    public OrderResponsePaymentLinkDto createOrder(Order order, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto) {
        try {
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

            String paymentLink = "";
            if (savedOrder.getPaymentMethod() == PaymentMethod.CASH) {
                paymentLink = "payment Link will be generated at the time od Delivery";
            } else {
                paymentLink = getPaymentLink(order).getPaymentLink();
            }
            OrderResponsePaymentLinkDto orderResponseDto = dtoMapper.toOrderResponsePaymentLinkDto(savedOrder, paymentLink);
            return orderResponseDto;
        } catch (Exception exception) {
            log.error("Error occurred");
            handleInventoryRevocation(order);
            throw exception;
        }
    }

    private InitiatePaymentResponseDto getPaymentLink(Order order) {
        UserResponseDto userResponseDto = userManagementServiceClient.getUserById(order.getUserId());
        UserDto user = new UserDto();
        user.setName(userResponseDto.getFirstName() + " " + userResponseDto.getLastName());
        user.setEmail(userResponseDto.getEmail());
        user.setPhoneNumber(userResponseDto.getPhoneNumber());
        user.setUserId(order.getUserId());

        InitiatePaymentRequestDto requestDto = new InitiatePaymentRequestDto();
        requestDto.setOrderNumber(order.getId());
        requestDto.setOrderId(order.getOrderId());
        requestDto.setAmount(order.getTotalAmount());
        requestDto.setCurrency("INR");
        requestDto.setUser(user);
        requestDto.setPaymentMode(order.getPaymentMethod().toString());
        requestDto.setPaymentGateway("RAZORPAY");

        return paymentServiceClient.initiatePayment(requestDto).getBody();
    }

    @Transactional
    @Override
    public Order updateOrder(Long id, UpdateOrderDto updateOrderDto, ValidateServiceTokenRequestDto validateServiceTokenRequestDto) {
//        validateUser(updateOrderDto.getUserId(), validateAndRefreshTokenRequestDto);
        tokenValidation.validateServiceToken(validateServiceTokenRequestDto);
        Order order = orderRepository.findByIdAndOrderStatus(id, OrderStatus.PLACED)
                .orElseThrow(() -> new OrderNotFound("Order Not found or already updated with payment status."));

        try {
            if (PaymentStatus.valueOf(updateOrderDto.getPaymentStatus().trim().toUpperCase()) == PaymentStatus.PENDING) {
                throw new InvalidPaymentStatus("Payment status cannot be PENDING");
            }
            order.setPaymentStatus(PaymentStatus.valueOf(updateOrderDto.getPaymentStatus().trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentStatus("Invalid payment status");
        }
        order.setUpdatedAt(new Date());

        Order savedOrder = orderRepository.save(order);
        log.info("Updated order {}", savedOrder);
        ReservationRevokeType reservationRevokeType = order.getPaymentStatus() == PaymentStatus.COMPLETED?
                ReservationRevokeType.COMPLETED : ReservationRevokeType.CANCELLED;
        for (OrderItem item : order.getOrderItems()) {
            RevokeInventoryReservationDto revokeInventoryReservationDto = new RevokeInventoryReservationDto();
            revokeInventoryReservationDto.setReservationId(item.getReservationId());
            revokeInventoryReservationDto.setOrderId(savedOrder.getOrderId());
            revokeInventoryReservationDto.setRevokeType(reservationRevokeType.toString());

            inventoryServiceClient.revokeReservation(revokeInventoryReservationDto);
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

    @Override
    public Order updateOrderStatus(Long id, UpdateOrderStatusDto updateOrderStatusDto, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto) {
        validateUser(updateOrderStatusDto.getUserId(), validateAndRefreshTokenRequestDto);
        Order order = orderRepository.findByIdAndOrderStatusNotIn(id, List.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED))
                .orElseThrow(() -> new OrderNotFound("Order Not found or Order may be cancelled or Delivered"));

        TrackingStatus trackingStatus;
        try {
            trackingStatus = TrackingStatus.valueOf(updateOrderStatusDto.getStatus().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidFieldException("Invalid tracking status");
        }

        if (order.getOrderTracking().getCurrentStatus() == trackingStatus) {
            return order;
        }

        if (trackingStatus == TrackingStatus.DELIVERED) {
            order.setOrderStatus(OrderStatus.DELIVERED);
        }

        Date now = new Date();
        order.getOrderTracking().setLastStatus(order.getOrderTracking().getCurrentStatus());
        order.getOrderTracking().setCurrentStatus(trackingStatus);
        order.getOrderTracking().setUpdatedAt(now);
        order.setUpdatedAt(now);

        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    @Override
    public Order getOrderTracking(String orderId) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(() -> new OrderNotFound("Order Not found"));
//        validateUser(order.getUserId(), validateAndRefreshTokenRequestDto);

        return order;
    }

    @Override
    public Order cancelOrder(String orderId, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto) {
        Order order = orderRepository.findByOrderIdAndOrderStatusNot(orderId, OrderStatus.CANCELLED).
                orElseThrow(() -> new OrderNotFound("Order Not found or Order may be Cancelled"));
        validateUser(order.getUserId(), validateAndRefreshTokenRequestDto);

        Date now = new Date();
        Date cancellationDeadline = new Date(order.getOrderTracking().getUpdatedAt().getTime() + deliveryCancellationTimeInMillis);
        if (now.after(cancellationDeadline)) {
            throw new CannotCancelException("Order cancellation date is after delivery cancellation deadline");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.getOrderTracking().setLastStatus(order.getOrderTracking().getCurrentStatus());
        order.getOrderTracking().setCurrentStatus(TrackingStatus.CANCELLED);
        order.setUpdatedAt(now);
        order.getOrderTracking().setUpdatedAt(now);

        Order savedOrder = null;
        try{
            log.info("Order Id: {}", orderId);
            savedOrder = orderRepository.save(order);
        } catch (Exception e){
            e.printStackTrace();
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
