package com.example.ordermanagementservice.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrderItem extends BaseModel {

    @Column(nullable = false)
    private long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String reservationId;

    @Column(nullable = false, columnDefinition = "text")
    private String productSnapshot;
}
