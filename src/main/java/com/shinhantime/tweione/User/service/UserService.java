package com.shinhantime.tweione.User.service;

import com.shinhantime.tweione.User.Jwt.dto.*;
import jakarta.transaction.Transactional;

public interface UserService {
    @Transactional
    JwtToken signIn(String username, String password);

    @Transactional
    UserDto signUp(SignUpDto signUpDto);

    realUserDto getUserById(Long id);  // 유저 조회 메서드
    void transferMoney(Long fromUserId, Long toUserId, Long amount);

    Wallet getNewWallet(String password);

    void chargeNewAccount(Wallet wallet, Long amount);
}
