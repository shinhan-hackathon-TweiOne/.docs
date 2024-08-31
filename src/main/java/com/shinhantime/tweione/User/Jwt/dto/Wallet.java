package com.shinhantime.tweione.User.Jwt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Wallet {
    private String walletAddress;
    private String walletFileName;
    private String walletPassword;
}
