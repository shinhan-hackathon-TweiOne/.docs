package com.shinhantime.tweione.Transaction.Repository.dto;


import com.shinhantime.tweione.Transaction.Repository.TransactionEntity;
import com.shinhantime.tweione.User.Jwt.dto.realUserDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDTO {
    private Long id;
    private realUserDto fromUser;
    private realUserDto toUser;
    private Long amount;
    private LocalDateTime timestamp;
    private String transactionHash;

    public static TransactionDTO fromEntity(TransactionEntity entity) {
        return TransactionDTO.builder()
                .id(entity.getId())
                .fromUser(realUserDto.fromEntity(entity.getFromUser()))
                .toUser(realUserDto.fromEntity(entity.getToUser()))
                .amount(entity.getAmount())
                .timestamp(entity.getTransactionDate())
                .transactionHash(entity.getTransactionHash())
                .build();
    }
}