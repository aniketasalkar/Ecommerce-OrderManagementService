package com.example.ordermanagementservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RevokeInventoryReservationDto {
    @NotBlank(message = "OrderId cannot be empty")
    private String orderId;

    @NotBlank(message = "reservationId cannot be empty")
    private String reservationId;

    @NotBlank(message = "Revoke type cannot be Empty (COMPLETE, CANCELLED)")
    private String revokeType;
}
