package com.example.ordermanagementservice.controllers;

import com.example.ordermanagementservice.dtos.OrderRequestDto;
import com.example.ordermanagementservice.dtos.OrderResponseDto;
import com.example.ordermanagementservice.services.IOrderService;
import com.example.ordermanagementservice.utils.IDtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @Autowired
    IDtoMapper dtoMapper;

    @PostMapping("/create_order")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid OrderRequestDto orderRequestDto) {
        OrderResponseDto orderResponseDto;
        try {
            orderResponseDto = dtoMapper.fromOrder(orderService.createOrder(dtoMapper.fromOrderRequestDto(orderRequestDto)));


            return new ResponseEntity<>(orderResponseDto, HttpStatus.CREATED);
        } catch (Exception exception) {
            throw exception;
        }
    }
}
