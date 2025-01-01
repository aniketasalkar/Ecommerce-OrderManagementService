package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.dtos.ValidateAndRefreshTokenRequestDto;
import com.example.ordermanagementservice.models.Order;

import java.util.List;

public interface IOrderService {
    Order createOrder(Order order, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
//    Order updateOrder(Order order);
    List<Order> getAllOrdersofUser(long userId, String filter, ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
}
