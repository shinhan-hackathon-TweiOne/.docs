package com.shinhantime.tweione.account.repository.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@Builder
public class AuthRequest {
    @JsonProperty("Header")
    private Header header;

    @JsonProperty("accountNo")
    private String accountNo;

    @JsonProperty("authText")
    private String authText;
}
