package com.shinhantime.tweione.User.Jwt.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthVerifyResponse {
    private int statusCode;
    private boolean isSuccess;
    private String message;
    private UserDto userDto;
    private JwtToken jwtToken;
}
