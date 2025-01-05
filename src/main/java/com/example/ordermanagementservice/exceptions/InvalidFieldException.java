package com.example.ordermanagementservice.exceptions;

public class InvalidFieldException extends RuntimeException {
    public InvalidFieldException(String message) {
        super(message);
    }
}
