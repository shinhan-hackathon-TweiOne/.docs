package com.shinhantime.tweione.User.Jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wallet {
    private String walletAddress;
    private String walletFileName;
    private String walletPassword;
    private String error;
}
