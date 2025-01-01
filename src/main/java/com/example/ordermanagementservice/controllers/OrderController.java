package com.example.ordermanagementservice.controllers;

import com.example.ordermanagementservice.dtos.OrderRequestDto;
import com.example.ordermanagementservice.dtos.OrderResponseDto;
import com.example.ordermanagementservice.dtos.ValidateAndRefreshTokenRequestDto;
import com.example.ordermanagementservice.services.IOrderService;
import com.example.ordermanagementservice.utils.IDtoMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            orderResponseDto = dtoMapper.fromOrder(orderService.createOrder(dtoMapper.fromOrderRequestDto(orderRequestDto),
                    dtoMapper.getValidateAndRefreshTokenRequestDto()));


            return new ResponseEntity<>(orderResponseDto, HttpStatus.CREATED);
        } catch (Exception exception) {
            throw exception;
        }
    }

    @GetMapping("/get_orders/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getAllOrdersByUser(@PathVariable Long userId,
                                                                     @RequestParam(value = "status", required = false, defaultValue = "ALL") String filter) {
        List<OrderResponseDto> orderResponseDtos;
        try {
            orderResponseDtos = dtoMapper.fromOrders(orderService.getAllOrdersofUser(userId, filter,
                    dtoMapper.getValidateAndRefreshTokenRequestDto()));

            return new ResponseEntity<>(orderResponseDtos, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }
}
