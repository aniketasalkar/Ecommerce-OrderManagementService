package com.example.ordermanagementservice.exceptions;

public class CannotCancelException extends RuntimeException {
    public CannotCancelException(String message) {
        super(message);
    }
}
