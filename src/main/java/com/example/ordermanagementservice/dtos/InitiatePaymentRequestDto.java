package com.example.ordermanagementservice.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class InitiatePaymentRequestDto {
    @NotNull(message = "orderNumber cannot be null and requires long type")
    private Long orderNumber;

    @NotBlank(message = "orderId cannot be null")
    private String orderId;

    @NotNull(message = "amount cannot be null")
    @Min(value = 1)
    @Max(value = 10000000)
    private Double amount;

    private String currency;

    private String paymentGateway;

    @NotBlank(message = "paymentMode cannot be blank")
    private String paymentMode;

    private UserDto user;
}
