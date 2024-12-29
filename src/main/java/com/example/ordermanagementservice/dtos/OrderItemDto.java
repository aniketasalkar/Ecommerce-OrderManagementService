package com.example.ordermanagementservice.dtos;

import com.example.ordermanagementservice.models.ProductSnapshot;
import lombok.Data;

@Data
public class OrderItemDto {

    private long productId;

    private int quantity;

    private double price;

    private ProductSnapshot productSnapshot;
}
