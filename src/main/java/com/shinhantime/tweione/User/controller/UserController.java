package com.shinhantime.tweione.User.controller;

import com.shinhantime.tweione.Config.SecurityUtil;
import com.shinhantime.tweione.User.Jwt.dto.*;
import com.shinhantime.tweione.User.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody SignInDto signInDto) {
        String username = signInDto.getUsername();
        String password = signInDto.getPassword();

        log.info("Attempting to sign in with username: {}", username);

        try {
            JwtToken jwtToken = userService.signIn(username, password);
            log.info("Sign-in successful for username: {}", username);
            log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
            return jwtToken;
        } catch (Exception e) {
            log.error("Sign-in failed for username: {}. Error: {}", username, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpDto signUpDto) {
        UserDto savedMemberDto = userService.signUp(signUpDto);
        return ResponseEntity.ok(savedMemberDto);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<realUserDto> getUserById(@PathVariable Long id) {
        realUserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequest transferRequest) {
        userService.transferMoney(transferRequest.getFromUserId(), transferRequest.getToUserId(), transferRequest.getAmount());
        return ResponseEntity.ok("Transfer successful");
    }


    @PostMapping("/test")
    public String test() {
        return SecurityUtil.getCurrentUsername();
    }

}