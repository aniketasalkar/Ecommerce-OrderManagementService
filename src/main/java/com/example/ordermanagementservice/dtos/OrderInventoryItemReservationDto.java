package com.example.ordermanagementservice.dtos;

import lombok.Data;

@Data
public class OrderInventoryItemReservationDto {
    private Long productId;

    private String productName;

//    private String sku;

    private Integer quantity;

    private Integer reservedQuantity;

    private Integer availableQuantity;
}
