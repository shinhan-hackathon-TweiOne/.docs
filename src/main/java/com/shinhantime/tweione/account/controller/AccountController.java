package com.shinhantime.tweione.account.controller;

import com.shinhantime.tweione.User.Jwt.dto.realUserDto;
import com.shinhantime.tweione.User.repository.UserEntity;
import com.shinhantime.tweione.account.repository.AccountEntity;
import com.shinhantime.tweione.account.repository.dto.AccountDTO;
import com.shinhantime.tweione.account.repository.dto.AuthDto;
import com.shinhantime.tweione.account.repository.dto.VerifayDto;
import com.shinhantime.tweione.account.service.AccountService;
import com.shinhantime.tweione.account.service.ApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final ApiService apiService;

    public AccountController(AccountService accountService, ApiService apiService) {
        this.accountService = accountService;
        this.apiService = apiService;
    }

    // Get list of accounts by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByUserId(@PathVariable Long userId) {
        List<AccountDTO> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    // Create a new account for a user
    @PostMapping("/user/{userId}")
    public AuthDto.AuthResponse sendAuth(
            @PathVariable Long userId,
            @RequestBody AuthDto.AuthRequest authRequest) throws Exception {

        apiService.sendAuth(authRequest.accountNo);

        return new AuthDto.AuthResponse(200, true, "송금에 성공했습니다");
    }

    @PostMapping("/user/{userId}/verify")
    public VerifayDto.VerifyResponse createAccount(
            @PathVariable Long userId,
            @RequestBody VerifayDto.VerifyRequest verifyRequest) throws Exception {

        apiService.verifyAuth(verifyRequest.getAccountNo(), verifyRequest.getAuthCode());
        accountService.createOrUpdateAccount(userId, verifyRequest.getAccountNo(), verifyRequest.getBankName());

        return new VerifayDto.VerifyResponse(200, true, "계좌등록에 성공했습니다.");

    }

    // Set a representative account for a user
    @PostMapping("/user/{userId}/main-account/{accountId}")
    public ResponseEntity<realUserDto> setRepresentativeAccount(
            @PathVariable Long userId,
            @PathVariable Long accountId) {

        realUserDto user = accountService.setMainAccount(userId, accountId);
        return ResponseEntity.ok(user);
    }

}