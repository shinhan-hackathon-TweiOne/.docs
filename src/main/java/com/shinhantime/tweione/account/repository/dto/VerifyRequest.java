package com.shinhantime.tweione.account.repository.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerifyRequest {
    @JsonProperty("Header")
    private Header header;

    @JsonProperty("accountNo")
    private String accountNo;

    @JsonProperty("authText")
    private String authText;

    @JsonProperty("authCode")
    private String authCode;
}

