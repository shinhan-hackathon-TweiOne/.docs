package com.shinhantime.tweione.User.service;

import com.shinhantime.tweione.User.Jwt.dto.JwtToken;
import com.shinhantime.tweione.User.Jwt.dto.SignUpDto;
import com.shinhantime.tweione.User.Jwt.dto.UserDto;
import jakarta.transaction.Transactional;

public interface UserService {
    @Transactional
    JwtToken signIn(String username, String password);

    @Transactional
    UserDto signUp(SignUpDto signUpDto);
}
