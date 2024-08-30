package com.shinhantime.tweione.User.service;


import com.shinhantime.tweione.Transaction.Repository.TransactionEntity;
import com.shinhantime.tweione.Transaction.Repository.TransactionRepository;
import com.shinhantime.tweione.User.Jwt.JwtTokenProvider;
import com.shinhantime.tweione.User.Jwt.dto.JwtToken;
import com.shinhantime.tweione.User.Jwt.dto.SignUpDto;
import com.shinhantime.tweione.User.Jwt.dto.UserDto;
import com.shinhantime.tweione.User.Jwt.dto.realUserDto;
import com.shinhantime.tweione.User.repository.UserEntity;
import com.shinhantime.tweione.User.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public JwtToken signIn(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("User authorities: {}", authentication.getAuthorities());
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        return jwtToken;
    }

    @Transactional
    @Override
    public UserDto signUp(SignUpDto signUpDto){
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return UserDto.toDto(userRepository.findByUsername(signUpDto.getUsername()).orElse(null));
        }
        // Password 암호화
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("USER");  // USER 권한 부여
        return UserDto.toDto(userRepository.save(signUpDto.toEntity(encodedPassword, roles)));
    }

    @Override
    public realUserDto getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return realUserDto.convertToDTO(user);
    }

    @Override
    public void transferMoney(Long fromUserId, Long toUserId, Long amount) {
        UserEntity fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        UserEntity toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        if (fromUser.getCurrentMoney() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        fromUser.setCurrentMoney(fromUser.getCurrentMoney() - amount);
        toUser.setCurrentMoney(toUser.getCurrentMoney() + amount);

        userRepository.save(fromUser);
        userRepository.save(toUser);

        // 송금 내역 저장
        TransactionEntity transaction = TransactionEntity.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .amount(amount)
                .build();

        transactionRepository.save(transaction);

        log.info("Transferred {} from {} to {}", amount, fromUser.getUsername(), toUser.getUsername());
    }

}