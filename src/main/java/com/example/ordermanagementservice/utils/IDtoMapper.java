package com.example.ordermanagementservice.utils;

import com.example.ordermanagementservice.dtos.OrderRequestDto;
import com.example.ordermanagementservice.dtos.OrderResponseDto;
import com.example.ordermanagementservice.dtos.ValidateAndRefreshTokenRequestDto;
import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderItem;

import java.util.List;

public interface IDtoMapper {
    Order fromOrderRequestDto(OrderRequestDto orderRequestDto);
    OrderResponseDto fromOrder(Order order);
    List<OrderResponseDto> fromOrders(List<Order> orders);
    ValidateAndRefreshTokenRequestDto getValidateAndRefreshTokenRequestDto();
//    List<OrderItem> fromCreateOrderRequestDto(OrderRequestDto orderRequestDto);
}
