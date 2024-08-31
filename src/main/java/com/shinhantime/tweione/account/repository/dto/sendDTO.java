package com.shinhantime.tweione.account.repository.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class sendDTO {
    @JsonProperty("Header")
    private Header header;

    @JsonProperty("depositAccountNo")
    private String depositAccountNo;

    @JsonProperty("depositTransactionSummary")
    private String depositTransactionSummary;
    @JsonProperty("transactionBalance")
    private String transactionBalance;
    @JsonProperty("withdrawalAccountNo")
    private String withdrawalAccountNo;
    @JsonProperty("withdrawalTransactionSummary")
    private String withdrawalTransactionSummary;
}
