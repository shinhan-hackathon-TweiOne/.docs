package com.shinhantime.tweione.account.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class VerifayDto {
    @Getter
    @AllArgsConstructor
    public static class VerifyRequest {
        public String accountNo;
        public String bankName;
        public String authCode;
    }

    @Getter
    @AllArgsConstructor
    public static class VerifyResponse{
        private int statusCode;
        private boolean isSuccess;
        private String message;
    }
}
