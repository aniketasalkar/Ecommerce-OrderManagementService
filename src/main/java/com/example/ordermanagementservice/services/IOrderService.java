package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.dtos.UpdateOrderDto;
import com.example.ordermanagementservice.dtos.UpdateOrderStatusDto;
import com.example.ordermanagementservice.dtos.ValidateAndRefreshTokenRequestDto;
import com.example.ordermanagementservice.models.Order;

import java.util.List;

public interface IOrderService {
    Order createOrder(Order order, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
    Order updateOrder(Long id, UpdateOrderDto updateOrderDto, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
    List<Order> getAllOrdersofUser(long userId, String filter, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
    Order updateOrderStatus(Long id, UpdateOrderStatusDto updateOrderStatusDto, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
    Order getOrderTracking(String orderId, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
    Order cancelOrder(String orderId, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
}
