package com.shinhantime.tweione.account.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.shinhantime.tweione.account.repository.AccountEntity;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private String bankName;
    private Long userId;

    public static  AccountDTO convertToDTO(AccountEntity accountEntity) {
        return AccountDTO.builder()
                .id(accountEntity.getId())
                .accountNumber(accountEntity.getAccountNumber())
                .bankName(accountEntity.getBankName())
                .userId(accountEntity.getUser().getId())
                .build();
    }
}