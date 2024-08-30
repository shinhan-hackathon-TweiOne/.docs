package com.shinhantime.tweione.account.service;

import com.shinhantime.tweione.User.repository.UserRepository;
import com.shinhantime.tweione.User.service.UserService;
import com.shinhantime.tweione.account.repository.AccountEntity;
import com.shinhantime.tweione.account.repository.AccountRepository;
import com.shinhantime.tweione.account.repository.dto.AccountDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.shinhantime.tweione.User.repository.UserEntity;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AccountDTO> getAccountsByUserId(Long userId) {
        List<AccountEntity> accountEntities = accountRepository.findByUserId(userId);

        // Convert the list of AccountEntity to a list of AccountDTO using the static method
        return accountEntities.stream()
                .map(AccountDTO::convertToDTO) // Convert each AccountEntity to AccountDTO
                .collect(Collectors.toList()); // Collect the results into a List
    }

    @Override
    public AccountEntity createOrUpdateAccount(Long userId, String accountNumber, String bankName) {
        UserEntity user = userRepository.findById(userId).orElse(null);

        AccountEntity account = AccountEntity.builder()
                .accountNumber(accountNumber)
                .bankName(bankName)
                .user(user)
                .build();

        return accountRepository.save(account);
    }

    @Override
    public UserEntity setMainAccount(Long userId, Long accountId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Account does not belong to the user");
        }

        user.setMainAccount(account);
        return userRepository.save(user);
    }


}