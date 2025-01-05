package com.example.ordermanagementservice.utils;

import com.example.ordermanagementservice.dtos.OrderRequestDto;
import com.example.ordermanagementservice.dtos.OrderResponseDto;
import com.example.ordermanagementservice.dtos.OrderTrackingResponseDto;
import com.example.ordermanagementservice.dtos.ValidateAndRefreshTokenRequestDto;
import com.example.ordermanagementservice.models.Order;

import java.util.List;

public interface IDtoMapper {
    Order toOrder(OrderRequestDto orderRequestDto);
    OrderResponseDto toOrderResponseDto(Order order);
    List<OrderResponseDto> toOrderResponseDtoList(List<Order> orders);
    ValidateAndRefreshTokenRequestDto getValidateAndRefreshTokenRequestDto();
    OrderTrackingResponseDto toOrderTrackingResponseDto(Order order);
//    List<OrderItem> fromCreateOrderRequestDto(OrderRequestDto orderRequestDto);
}
