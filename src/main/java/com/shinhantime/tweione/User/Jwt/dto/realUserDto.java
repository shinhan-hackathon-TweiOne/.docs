package com.shinhantime.tweione.User.Jwt.dto;

import com.shinhantime.tweione.User.repository.UserEntity;
import com.shinhantime.tweione.account.repository.dto.AccountDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class realUserDto {
    private Long id;
    private String username;
    private String name;
    private Long currentMoney;
    private List<String> roles;
    private AccountDTO mainAccount; // mainAccount를 AccountDTO로 포함

    public static realUserDto convertToDTO(UserEntity userEntity) {
        return realUserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .name(userEntity.getName())
                .currentMoney(userEntity.getCurrentMoney())
                .roles(userEntity.getRoles())
                .mainAccount(userEntity.getMainAccount() != null
                        ? AccountDTO.convertToDTO(userEntity.getMainAccount())
                        : null)
                .build();
    }
}