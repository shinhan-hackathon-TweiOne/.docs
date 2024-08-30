package com.shinhantime.tweione.User.Jwt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthRegisterDto {
    private String phoneNumber;
    private String name;
    private String AuthCode;
}
