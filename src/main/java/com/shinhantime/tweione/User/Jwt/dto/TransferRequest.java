package com.shinhantime.tweione.User.Jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private Long fromUserId; // 송금하는 유저의 ID
    private Long toUserId;   // 송금을 받는 유저의 ID
    private Long amount;     // 송금 금액
}