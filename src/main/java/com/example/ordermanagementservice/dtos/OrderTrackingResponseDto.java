package com.example.ordermanagementservice.dtos;

import com.example.ordermanagementservice.models.DeliverySnapshot;
import lombok.Data;

import java.util.Date;

@Data
public class OrderTrackingResponseDto {
    private String orderId;
    private Long trackingId;
    private String currentStatus;
    private Date expectedDelivery;
    private DeliverySnapshot deliverySnapshot;
}
