package com.example.ordermanagementservice.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class InventoryReservationResponseDto {
    private String reservationId;

    private Long productId;

    private Integer quantity;

    private String orderId;

    //    private Long inventoryItemId;
    private String inventoryItemResponse;

    private Date reservationDate;

    private Date expirationDate;

    private String inventoryReservationStatus;
}
