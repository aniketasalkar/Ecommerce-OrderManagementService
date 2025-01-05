package com.example.ordermanagementservice.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class UpdateOrderStatusDto {

    @NotBlank(message = "orderId cannot be blank.")
    private String orderId;

    @Min(value = 1, message = "userId cannot be negative")
    @NotNull(message = "userId cannot be empty")
    private long userId;

    @NotEmpty(message = "order status cannot be blank.")
    private String status;
}
