package com.shinhantime.tweione.User.controller;

import com.shinhantime.tweione.Config.SecurityUtil;
import com.shinhantime.tweione.User.Jwt.dto.JwtToken;
import com.shinhantime.tweione.User.Jwt.dto.SignInDto;
import com.shinhantime.tweione.User.Jwt.dto.SignUpDto;
import com.shinhantime.tweione.User.Jwt.dto.UserDto;
import com.shinhantime.tweione.User.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
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
            throw e; // 재던지기를 통해 에러를 처리하는 로직을 이어갈 수 있습니다.
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpDto signUpDto) {
        UserDto savedMemberDto = userService.signUp(signUpDto);
        return ResponseEntity.ok(savedMemberDto);
    }

    @PostMapping("/test")
    public String test() {
        return SecurityUtil.getCurrentUsername();
    }

}