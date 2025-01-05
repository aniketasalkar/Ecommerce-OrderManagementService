package com.example.ordermanagementservice.utils;

import com.example.ordermanagementservice.clients.KafkaProducerClient;
import com.example.ordermanagementservice.dtos.ReservationRevokeType;
import com.example.ordermanagementservice.models.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaEventGenerator {

    @Autowired
    KafkaProducerClient kafkaProducerClient;

    @Autowired
    ObjectMapper objectMapper;

}
