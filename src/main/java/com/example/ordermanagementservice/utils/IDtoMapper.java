package com.example.ordermanagementservice.utils;

import com.example.ordermanagementservice.dtos.*;
import com.example.ordermanagementservice.models.Order;

import java.util.List;

public interface IDtoMapper {
    Order toOrder(OrderRequestDto orderRequestDto);
    OrderResponseDto toOrderResponseDto(Order order);
    List<OrderResponseDto> toOrderResponseDtoList(List<Order> orders);
    ValidateAndRefreshTokenRequestDto getValidateAndRefreshTokenRequestDto();
    OrderTrackingResponseDto toOrderTrackingResponseDto(Order order);
    OrderResponsePaymentLinkDto toOrderResponsePaymentLinkDto(Order order, String paymentLink);
    ValidateServiceTokenRequestDto toValidateServiceTokenRequestDto();
//    List<OrderItem> fromCreateOrderRequestDto(OrderRequestDto orderRequestDto);
}
