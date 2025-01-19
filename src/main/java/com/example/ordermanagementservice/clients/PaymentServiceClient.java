package com.example.ordermanagementservice.clients;

import com.example.ordermanagementservice.dtos.InitiatePaymentRequestDto;
import com.example.ordermanagementservice.dtos.InitiatePaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENTSERVICE")
public interface PaymentServiceClient {
    @PostMapping("/api/payment/initiate_payment")
    public ResponseEntity<InitiatePaymentResponseDto> initiatePayment(@RequestBody InitiatePaymentRequestDto initiatePaymentRequestDto);
}
