package com.example.ordermanagementservice.clients;

import com.example.ordermanagementservice.dtos.ServiceRegistryResponseDto;
import com.example.ordermanagementservice.dtos.ValidateAndRefreshTokenRequestDto;
import com.example.ordermanagementservice.dtos.ValidateServiceTokenRequestDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("AUTHENTICATIONSERVICE")
public interface UserAuthServiceClient {
    @PostMapping("/api/auth/{email}/validateToken")
    Boolean validateToken(@PathVariable String email,
                          @RequestBody @Valid ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);

    @PostMapping("/api/auth/service/validate_token/")
    ResponseEntity<Boolean> validateServiceRegistryToken(
            @RequestBody ValidateServiceTokenRequestDto validateServiceTokenRequestDto);

    @GetMapping("/api/auth/service/fetch_token/{serviceName}")
    ResponseEntity<ServiceRegistryResponseDto> fetchServiceRegistryToken(@PathVariable String serviceName);
}
