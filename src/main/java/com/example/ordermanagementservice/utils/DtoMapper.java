package com.example.ordermanagementservice.utils;

import com.example.ordermanagementservice.dtos.OrderRequestDto;
import com.example.ordermanagementservice.dtos.OrderResponseDto;
import com.example.ordermanagementservice.dtos.OrderItemDto;
import com.example.ordermanagementservice.exceptions.UnsupportedPaymentMethod;
import com.example.ordermanagementservice.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DtoMapper implements IDtoMapper {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Order fromOrderRequestDto(OrderRequestDto orderRequestDto) {
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
    public OrderResponseDto fromOrder(Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setUserId(order.getUserId());
        orderResponseDto.setTotalAmount(order.getTotalAmount());
        orderResponseDto.setOrderStatus(order.getOrderStatus().toString());
        orderResponseDto.setPaymentStatus(order.getPaymentStatus().toString());
        orderResponseDto.setPaymentMethod(order.getPaymentMethod().toString());
        orderResponseDto.setOrderDate(order.getOrderDate());
        orderResponseDto.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        orderResponseDto.setDeliverySnapshot(objectMapper.convertValue(order.getDeliverySnapshot(), DeliverySnapshot.class));

        List<OrderItemDto> orderItems = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setProductId(orderItem.getProductId());
            orderItemDto.setQuantity(orderItem.getQuantity());
            orderItemDto.setPrice(orderItem.getPrice());
            orderItemDto.setProductSnapshot(objectMapper.convertValue(orderItem.getProductSnapshot(), ProductSnapshot.class));
            orderItems.add(orderItemDto);
        }

        orderResponseDto.setOrderItems(orderItems);

        return orderResponseDto;
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
