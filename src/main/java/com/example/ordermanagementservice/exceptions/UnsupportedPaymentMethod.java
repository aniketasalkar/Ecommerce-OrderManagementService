package com.example.ordermanagementservice.exceptions;

public class UnsupportedPaymentMethod extends RuntimeException {
    public UnsupportedPaymentMethod(String message) {
        super(message);
    }
}
