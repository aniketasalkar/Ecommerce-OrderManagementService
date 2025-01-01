package com.example.ordermanagementservice.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ValidateAndRefreshTokenRequestDto {
    @NotEmpty(message = "AccessToken Required")
    private String accessToken;

    @NotEmpty(message = "RefreshToken Required")
    private String refreshToken;
}
