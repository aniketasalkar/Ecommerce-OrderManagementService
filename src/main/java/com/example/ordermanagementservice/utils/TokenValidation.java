package com.example.ordermanagementservice.utils;

import com.example.ordermanagementservice.clients.UserAuthServiceClient;
import com.example.ordermanagementservice.dtos.ServiceRegistryResponseDto;
import com.example.ordermanagementservice.dtos.ValidateServiceTokenRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenValidation {
    @Autowired
    UserAuthServiceClient userAuthServiceClient;

    public void validateServiceToken(ValidateServiceTokenRequestDto requestDto) {
        log.info("Validating service token");
        userAuthServiceClient.validateServiceRegistryToken(requestDto);
        log.info("Service registry token validated");
    }

//    public ServiceRegistryResponseDto fetchToken(Long id) {
//        ServiceRegistryResponseDto serviceRegistryResponseDto = userAuthServiceClient.fetchServiceRegistryToken(id).getBody()
//        return ;
//    }
}
