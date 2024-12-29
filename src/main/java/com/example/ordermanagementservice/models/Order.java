package com.example.ordermanagementservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class Order extends BaseModel {

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private double totalAmount;

    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Column(nullable = false)
    private Date orderDate;

    @Column(nullable = false)
    private Date expectedDeliveryDate;

    @Column(nullable = false, columnDefinition = "text")
    private String deliverySnapshot;

    @OneToOne
    private OrderTracking orderTracking;
}
