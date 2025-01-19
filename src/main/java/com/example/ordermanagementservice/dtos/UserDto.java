package com.example.ordermanagementservice.dtos;

import lombok.Data;

@Data
public class UserDto {
    private String name;
    private String email;
    private String phoneNumber;
    private Long userId;
}
