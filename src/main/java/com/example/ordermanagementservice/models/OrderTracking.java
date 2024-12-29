package com.example.ordermanagementservice.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrderTracking extends BaseModel {

    @Enumerated(EnumType.STRING)
    private TrackingStatus currentStatus;

    @Enumerated(EnumType.STRING)
    private TrackingStatus lastStatus;
}
