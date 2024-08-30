package com.shinhantime.tweione.account.repository.dto;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@Builder
public class Header {
    @JsonProperty("apiName")
    private String apiName;

    @JsonProperty("transmissionDate")
    private String transmissionDate;

    @JsonProperty("transmissionTime")
    private String transmissionTime;

    @JsonProperty("institutionCode")
    private String institutionCode;

    @JsonProperty("fintechAppNo")
    private String fintechAppNo;

    @JsonProperty("apiServiceCode")
    private String apiServiceCode;

    @JsonProperty("institutionTransactionUniqueNo")
    private String institutionTransactionUniqueNo;

    @JsonProperty("apiKey")
    private String apiKey;

    @JsonProperty("userKey")
    private String userKey;
}