package com.example.ordermanagementservice.exceptions;

public class InvalidPaymentStatus extends RuntimeException {
    public InvalidPaymentStatus(String message) {
        super(message);
    }
}
