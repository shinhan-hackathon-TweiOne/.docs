package com.shinhantime.tweione.User.Jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private int statusCode;
    private boolean isSuccess;
    private String message;
}