package com.shinhantime.tweione.account.repository.dto;


import com.shinhantime.tweione.User.Jwt.dto.JwtToken;
import com.shinhantime.tweione.User.Jwt.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class AuthDto {
    @Getter
    @AllArgsConstructor
    public static class AuthRequest {
        public String accountNo;
        public String bankName;
    }

    @Getter
    @AllArgsConstructor
    public static class AuthResponse{
        private int statusCode;
        private boolean isSuccess;
        private String message;
    }

}
