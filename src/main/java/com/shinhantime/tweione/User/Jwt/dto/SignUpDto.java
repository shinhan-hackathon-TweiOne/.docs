package com.shinhantime.tweione.User.Jwt.dto;

import com.shinhantime.tweione.User.repository.UserEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {
    private String username;
    private String password;
    private String name;
    private List<String> roles = new ArrayList<>();

    public UserEntity toEntity(String encodedPassword, List<String> roles, Wallet wallet ){
        return UserEntity.builder()
                .username(username)
                .password(encodedPassword)
                .name(name)
                .currentMoney(1000000L)
                .roles(roles)
                .walletAddress(wallet.getWalletAddress())
                .walletKeyStore(wallet.getWalletFileName())
                .build();
    }

}
