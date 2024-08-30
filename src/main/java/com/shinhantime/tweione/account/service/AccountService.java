package com.shinhantime.tweione.account.service;

import com.shinhantime.tweione.User.Jwt.dto.realUserDto;
import com.shinhantime.tweione.account.repository.AccountEntity;
import com.shinhantime.tweione.account.repository.dto.AccountDTO;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAccountsByUserId(Long userId);

    AccountEntity createOrUpdateAccount(Long userId, String accountNumber, String bankName);

    realUserDto setMainAccount(Long userId, Long accountId);
}
