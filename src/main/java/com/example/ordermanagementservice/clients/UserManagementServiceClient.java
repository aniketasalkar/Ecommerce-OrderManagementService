package com.example.ordermanagementservice.clients;

import com.example.ordermanagementservice.dtos.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("USERMANAGEMENTSERVICE")
public interface UserManagementServiceClient {
    @GetMapping("/api/users/{userId}")
    UserResponseDto getUserById(@PathVariable long userId);
}
