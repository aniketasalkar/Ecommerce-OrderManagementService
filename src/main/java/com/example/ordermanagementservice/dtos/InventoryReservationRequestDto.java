package com.example.ordermanagementservice.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryReservationRequestDto {
    @NotNull
    @Min(value = 1, message = "ProductId cannot be null")
    private Long productId;

    @NotNull
    @Min(value = 1, message = "Quantity cannot be 0 or negative")
    @Max(value = 10, message = "Exceeded maximum of 10")
    private Integer quantity;

    @NotBlank(message = "OrderId cannot be empty")
    private String orderId;
}
