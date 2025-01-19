package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.dtos.*;
import com.example.ordermanagementservice.models.Order;

import java.util.List;

public interface IOrderService {
    OrderResponsePaymentLinkDto createOrder(Order order, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
    Order updateOrder(Long id, UpdateOrderDto updateOrderDto, ValidateServiceTokenRequestDto validateServiceTokenRequestDto);
    List<Order> getAllOrdersofUser(long userId, String filter, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
    Order updateOrderStatus(Long id, UpdateOrderStatusDto updateOrderStatusDto, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
    Order getOrderTracking(String orderId);
    Order cancelOrder(String orderId, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
}
