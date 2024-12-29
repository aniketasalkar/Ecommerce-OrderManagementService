package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.dtos.OrderResponseDto;
import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderItem;

import java.util.List;

public interface IOrderService {
    Order createOrder(Order order);
}
