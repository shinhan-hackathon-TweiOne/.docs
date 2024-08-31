package com.shinhantime.tweione.User.service;


import com.shinhantime.tweione.MyCashToken;
import com.shinhantime.tweione.Transaction.Repository.TransactionEntity;
import com.shinhantime.tweione.Transaction.Repository.TransactionRepository;
import com.shinhantime.tweione.User.Jwt.JwtTokenProvider;
import com.shinhantime.tweione.User.Jwt.dto.*;
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
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.web3j.crypto.WalletUtils.loadCredentials;

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

    // 블록체인 관련 변수
    private static Web3j web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));
    private static final String CONTRACT_ADDRESS = "0xdf956fd4164999c30a064e88de74607d57da1e9a";
    private static final String RELAY_KEYSTORE = "UTC--2024-08-29T08-15-21.922424300Z--41e4cf7b9f52f76f74e486337cc5164509ea9f0f.json";
    private static final String RELAY_PASSWORD = "ssafy";

    private static final BigInteger GAS_PRICE = BigInteger.valueOf(50000000000L); // 50 Gwei
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(8000000L); // 8,000,000 가스 한도
    private static final long CHAIN_ID = 1234; // 사용 중인 네트워크의 체인 ID

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

        // 블록체인 계좌 생성
        Wallet wallet = getNewWallet(encodedPassword);

        // 기본금 생성
        chargeNewAccount(wallet, 1000000L);

        List<String> roles = new ArrayList<>();
        roles.add("USER");  // USER 권한 부여
        return UserDto.toDto(userRepository.save(signUpDto.toEntity(encodedPassword, roles, wallet)));
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

    @Override
    public Wallet getNewWallet(String password) {
        String WALLET_DIRECTORY = "C:/Users/SSAFY/Blockchain/keystore"; // 지갑 파일 저장 디렉토리
        Wallet accountInfoVo = new Wallet();

        try {
            // 계정 생성
            String walletFileName = WalletUtils.generateNewWalletFile(password, new File(WALLET_DIRECTORY));

            // 생성된 계정의 Credentials를 로드합니다.
            Credentials credentials = loadCredentials(password, WALLET_DIRECTORY + "/" + walletFileName);

            log.info("새 계정 생성됨: {}", credentials.getAddress());
            accountInfoVo.setWalletAddress(credentials.getAddress());
            accountInfoVo.setWalletFileName(walletFileName);
            accountInfoVo.setWalletPassword(password);

        } catch (Exception e) {
            log.error("지갑 생성 또는 로드 중 오류 발생: {}", e.getMessage());
            accountInfoVo.setError(e.getMessage());
        }

        return accountInfoVo;
    }

    public void chargeNewAccount(Wallet wallet, Long amount) {
        ContractGasProvider gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);

        try {
            Credentials adminCredentials = loadCredentials(RELAY_KEYSTORE, RELAY_PASSWORD);
            MyCashToken contract = MyCashToken.load(CONTRACT_ADDRESS, web3j, new RawTransactionManager(web3j, adminCredentials, CHAIN_ID), gasProvider);

            BigInteger chargeAmount = BigInteger.valueOf(amount);

            // 충전 전 잔액 조회
            BigInteger initialBalance = contract.balanceOf(wallet.getWalletAddress()).send();
            System.out.println("Initial Balance of Sender: " + initialBalance);

            // 충전
            TransactionReceipt receipt = contract.charge(wallet.getWalletAddress(), chargeAmount).send();

            // 충전 후 잔액 조회
            BigInteger newBalance = contract.balanceOf(wallet.getWalletAddress()).send();
            System.out.println("New Balance of Sender after Charge: " + newBalance);

        } catch (Exception e) {
            // 예외 처리
            System.err.println("지갑 충전 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Credentials loadCredentials(String keyStoreFileName, String password) throws Exception {
        return WalletUtils.loadCredentials(password, keyStoreFileName);
    }

}