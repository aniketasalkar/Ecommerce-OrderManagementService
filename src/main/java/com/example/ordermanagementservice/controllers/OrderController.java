package com.example.ordermanagementservice.controllers;

import com.example.ordermanagementservice.dtos.*;
import com.example.ordermanagementservice.services.IOrderService;
import com.example.ordermanagementservice.utils.IDtoMapper;
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
            orderResponseDto = dtoMapper.toOrderResponseDto(orderService.createOrder(dtoMapper.toOrder(orderRequestDto),
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
            orderResponseDtos = dtoMapper.toOrderResponseDtoList(orderService.getAllOrdersofUser(userId, filter,
                    dtoMapper.getValidateAndRefreshTokenRequestDto()));

            return new ResponseEntity<>(orderResponseDtos, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }

    @PostMapping("/orders/{id}/update_order")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id, @RequestBody @Valid UpdateOrderDto updateOrderDto) {
        OrderResponseDto orderResponseDto;
        try {
            orderResponseDto = dtoMapper.toOrderResponseDto(orderService.updateOrder(id,
                    updateOrderDto,
                    dtoMapper.getValidateAndRefreshTokenRequestDto()));

            return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }

    @PostMapping("/orders/{id}/update_status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long id, @RequestBody @Valid UpdateOrderStatusDto updateOrderStatusDto) {
        OrderResponseDto orderResponseDto;

        try {
            orderResponseDto = dtoMapper.toOrderResponseDto(orderService.updateOrderStatus(id,
                    updateOrderStatusDto,
                    dtoMapper.getValidateAndRefreshTokenRequestDto()));

            return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }

    @GetMapping("/orders/tracking/{id}")
    public ResponseEntity<OrderTrackingResponseDto> getOrderTracking(@PathVariable String id) {
        OrderTrackingResponseDto orderTrackingResponseDto;
        try {
            orderTrackingResponseDto = dtoMapper.toOrderTrackingResponseDto(orderService.getOrderTracking(id,
                    dtoMapper.getValidateAndRefreshTokenRequestDto()));

            return new ResponseEntity<>(orderTrackingResponseDto, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }

    @DeleteMapping("/orders/cancel/{id}")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable String id) {
        OrderResponseDto orderResponseDto;
        try {
            orderResponseDto = dtoMapper.toOrderResponseDto(orderService.cancelOrder(id,
                    dtoMapper.getValidateAndRefreshTokenRequestDto()));

            return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }
}
