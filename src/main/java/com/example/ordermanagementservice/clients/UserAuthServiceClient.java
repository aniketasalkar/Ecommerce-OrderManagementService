package com.example.ordermanagementservice.clients;

import com.example.ordermanagementservice.dtos.ValidateAndRefreshTokenRequestDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("AUTHENTICATIONSERVICE")
public interface UserAuthServiceClient {
    @PostMapping("/api/auth/{email}/validateToken")
    Boolean validateToken(@PathVariable String email,
                          @RequestBody @Valid ValidateAndRefreshTokenRequestDto validateAndRefreshTokenRequestDto);
}
