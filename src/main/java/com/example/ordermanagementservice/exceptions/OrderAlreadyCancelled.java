package com.example.ordermanagementservice.exceptions;

public class OrderAlreadyCancelled extends RuntimeException {
    public OrderAlreadyCancelled(String message) {
        super(message);
    }
}
