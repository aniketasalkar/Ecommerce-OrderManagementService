package com.example.ordermanagementservice.utils;

import com.example.ordermanagementservice.dtos.*;
import com.example.ordermanagementservice.exceptions.TokenExpiredException;
import com.example.ordermanagementservice.exceptions.UnsupportedPaymentMethod;
import com.example.ordermanagementservice.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DtoMapper implements IDtoMapper {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public Order toOrder(OrderRequestDto orderRequestDto) {
        Order order = new Order();
        order.setUserId(orderRequestDto.getUserId());
        order.setTotalAmount(orderRequestDto.getTotalAmount());
        order.setOrderItems(fromCreateOrderRequestDto(orderRequestDto.getOrderItems()));
        try {
            order.setPaymentMethod(PaymentMethod.valueOf(orderRequestDto.getPaymentMethod().trim().toUpperCase()));
        } catch (UnsupportedPaymentMethod exception) {
            throw exception;
        }
        order.setExpectedDeliveryDate(orderRequestDto.getExpectedDeliveryDate());
        try {
            order.setDeliverySnapshot(objectMapper.writeValueAsString(orderRequestDto.getDeliverySnapshot()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return order;
    }

    @Override
    public OrderResponseDto toOrderResponseDto(Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setUserId(order.getUserId());
        orderResponseDto.setTotalAmount(order.getTotalAmount());
        orderResponseDto.setOrderStatus(order.getOrderStatus().toString());
        orderResponseDto.setPaymentStatus(order.getPaymentStatus().toString());
        orderResponseDto.setPaymentMethod(order.getPaymentMethod().toString());
        orderResponseDto.setOrderDate(order.getOrderDate());
        orderResponseDto.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        try {
            orderResponseDto.setDeliverySnapshot(objectMapper.readValue(order.getDeliverySnapshot(), DeliverySnapshot.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<OrderItemDto> orderItems = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setProductId(orderItem.getProductId());
            orderItemDto.setQuantity(orderItem.getQuantity());
            orderItemDto.setPrice(orderItem.getPrice());
            try {
                orderItemDto.setProductSnapshot(objectMapper.readValue(orderItem.getProductSnapshot(), ProductSnapshot.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            orderItems.add(orderItemDto);
        }

        orderResponseDto.setOrderItems(orderItems);

        return orderResponseDto;
    }

    @Override
    public List<OrderResponseDto> toOrderResponseDtoList(List<Order> orders) {
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderResponseDto orderResponseDto = toOrderResponseDto(order);
            orderResponseDtos.add(orderResponseDto);
        }

        return orderResponseDtos;
    }

    @Override
    public ValidateAndRefreshTokenRequestDto getValidateAndRefreshTokenRequestDto() {
        ValidateAndRefreshTokenRequestDto tokensDto = null;
        try {
            tokensDto = new ValidateAndRefreshTokenRequestDto();
            tokensDto.setAccessToken(httpServletRequest.getHeader("Set-Cookie").toString());
            tokensDto.setRefreshToken(httpServletRequest.getHeader("Set-Cookie2").toString());
        } catch (Exception e) {
            throw new TokenExpiredException("Token required");
        }

        return tokensDto;
    }

    @Override
    public OrderTrackingResponseDto toOrderTrackingResponseDto(Order order) {
        OrderTrackingResponseDto orderTrackingResponseDto = new OrderTrackingResponseDto();
        orderTrackingResponseDto.setTrackingId(order.getOrderTracking().getId());
        orderTrackingResponseDto.setOrderId(order.getOrderId());
        orderTrackingResponseDto.setCurrentStatus(order.getOrderTracking().getCurrentStatus().toString());
        orderTrackingResponseDto.setExpectedDelivery(order.getExpectedDeliveryDate());
        try {
            orderTrackingResponseDto.setDeliverySnapshot(objectMapper.readValue(order.getDeliverySnapshot(), DeliverySnapshot.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return orderTrackingResponseDto;
    }

    private List<OrderItem> fromCreateOrderRequestDto(List<OrderItemDto> orderItemDtos) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDto orderItemDto : orderItemDtos) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(orderItemDto.getProductId());
            orderItem.setQuantity(orderItemDto.getQuantity());
            orderItem.setPrice(orderItemDto.getPrice());
            try {
                orderItem.setProductSnapshot(objectMapper.writeValueAsString(orderItemDto.getProductSnapshot()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            orderItems.add(orderItem);
        }
        return orderItems;
    }
}
