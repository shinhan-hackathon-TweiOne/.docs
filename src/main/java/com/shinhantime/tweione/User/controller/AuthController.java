package com.shinhantime.tweione.User.controller;

import com.shinhantime.tweione.User.Jwt.dto.*;
import com.shinhantime.tweione.User.repository.UserRepository;
import com.shinhantime.tweione.User.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping()
public class AuthController {

    private final DefaultMessageService messageService;
    private final UserService userService;
    @Value("${api.coolsms.API_KEY}")
    private String apiKey;

    @Value("${api.coolsms.API_SECRET_KEY}")
    private String apiSecretKey;

    @Value("${api.coolsms.API_URL}")
    private String apiUrl;

    @Value("${api.coolsms.FROM_NUMBER}")
    private String fromNumber;

    private final UserRepository userRepository;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public AuthController(@Value("${api.coolsms.API_KEY}") String apiKey,
                          @Value("${api.coolsms.API_SECRET_KEY}") String apiSecretKey,
                          @Value("${api.coolsms.API_URL}") String apiUrl, UserService userService, UserRepository userRepository,
                          RedisTemplate<String, String> redisTemplate) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, apiUrl);
        this.redisTemplate = redisTemplate;
    }


    // 인증번호 전송
    @PostMapping("/auth")
    public AuthResponse sendAuthCode(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        System.out.println(phoneNumber);
        System.out.println(fromNumber);
        String authCode = generateAuthCode();
        redisTemplate.opsForValue().set(phoneNumber, authCode, 5, TimeUnit.MINUTES);

        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(phoneNumber);
        message.setText("[신한타임] 인증 코드 [" + authCode + "]를 입력해주세요.");
        System.out.println("[신한타임] 인증 코드 [" + authCode + "]를 입력해주세요.");
        return new AuthResponse(200,true, "메세지 전송에 성공하였습니다.");
    }

    // 인증번호 전송
    @PostMapping("/auth-test")
    public AuthResponse sendAuthTestCode(@RequestBody AuthDto authDto) {
        String phoneNumber = authDto.getPhoneNumber();
        System.out.println(phoneNumber);
        System.out.println(fromNumber);
        String authCode = generateAuthCode();
        redisTemplate.opsForValue().set(phoneNumber, authCode, 5, TimeUnit.MINUTES);
        System.out.println("[신한타임] 인증 코드 [" + authCode + "]를 입력해주세요.");
        return new AuthResponse(200,true, "[신한타임] 인증 코드 [" + authCode + "]를 입력해주세요.");
    }

    @PostMapping("/verify-auth")
    public AuthVerifyResponse verifyAuthCode(@RequestBody AuthRegisterDto authRegisterDto) {
        String phoneNumber =authRegisterDto.getPhoneNumber();
        String authCode = authRegisterDto.getAuthCode();
        String storedAuthCode = redisTemplate.opsForValue().get(phoneNumber);

        if (storedAuthCode != null && storedAuthCode.equals(authCode)) {
            SignUpDto signUpDto = SignUpDto.builder()
                    .username(authRegisterDto.getPhoneNumber())
                    .name(authRegisterDto.getName())
                    .password("1234")
                    .build();
            userService.signUp(signUpDto);

            String username = authRegisterDto.getPhoneNumber();
            String password = "1234";

            log.info("Attempting to sign in with username: {}", username);

            try {
                JwtToken jwtToken = userService.signIn(username, password);
                log.info("Sign-in successful for username: {}", username);
                log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
                UserDto userDto = UserDto.toDto(userRepository.findByUsername(username).orElse(null));

                return new AuthVerifyResponse(200, true, "인증번호가 일치합니다.", userDto, jwtToken);
            } catch (Exception e) {
                log.error("Sign-in failed for username: {}. Error: {}", username, e.getMessage());
                throw e;
            }
        } else {
            return new AuthVerifyResponse(401, false, "인증번호가 불일치합니다.", null, null);
        }
    }

    private String generateAuthCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}