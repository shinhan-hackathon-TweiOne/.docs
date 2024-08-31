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
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private static ContractGasProvider GAS_PROVIDER = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
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

        // 블록 체인 거래
        fundSenderAccount(fromUser.getWallet());
        String transactionHash = transfer(fromUser.getWallet(), toUser.getWallet());

        fromUser.setCurrentMoney(fromUser.getCurrentMoney() - amount);
        toUser.setCurrentMoney(toUser.getCurrentMoney() + amount);

        userRepository.save(fromUser);
        userRepository.save(toUser);

        // 송금 내역 저장
        TransactionEntity transaction = TransactionEntity.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .amount(amount)
                .transactionHash(transactionHash)
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

    private void sendEther(Credentials senderCredentials, String recipientAddress, BigInteger amount) throws Exception {
        // Get the nonce
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                senderCredentials.getAddress(),
                org.web3j.protocol.core.DefaultBlockParameterName.LATEST
        ).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        // Create a transaction to send Ether
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, // nonce
                GAS_PRICE, // Gas price
                GAS_LIMIT, // Gas limit
                recipientAddress, // Recipient address
                amount // Amount in Wei
        );

        // Sign the transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, CHAIN_ID, senderCredentials);
        String signedTransaction = Numeric.toHexString(signedMessage);

        // Send the transaction
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedTransaction).send();
        String transactionHash = ethSendTransaction.getTransactionHash();
        System.out.println("Transaction Hash: " + transactionHash);
    }

    private String transferTokens(Credentials credentials, String contractAddress, String recipientAddress, BigInteger amount) throws Exception {
        // Create the transfer function call
        Function transferFunction = new Function(
                "transfer",
                Arrays.asList(new Address(recipientAddress), new Uint256(amount)),
                Collections.emptyList()
        );

        // Encode the function call
        String encodedFunction = FunctionEncoder.encode(transferFunction);

        // Get the nonce
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                credentials.getAddress(),
                org.web3j.protocol.core.DefaultBlockParameterName.LATEST
        ).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        // Create RawTransaction
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce, // nonce
                GAS_PRICE,
                GAS_LIMIT,
                contractAddress,
                BigInteger.ZERO, // value in Wei, zero for ERC20 tokens
                encodedFunction
        );

        // Sign the transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, CHAIN_ID, credentials);
        String signedTransaction = Numeric.toHexString(signedMessage);

        // Send the transaction
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedTransaction).send();
        String transactionHash = ethSendTransaction.getTransactionHash();
        System.out.println("Transaction Hash: " + transactionHash);

        return transactionHash;
    }

    private void fundSenderAccount(Wallet sender) {
        try {
            Credentials funderCredentials = loadCredentials(RELAY_KEYSTORE, RELAY_PASSWORD);
            Credentials senderCredentials = loadCredentials(sender.getWalletFileName(), sender.getWalletPassword());

            // 전송할 이더 양
            BigInteger amount = BigInteger.valueOf(1_000_000_000_000_000_000L); // 1 ETH in Wei

            sendEther(funderCredentials, senderCredentials.getAddress(), amount);
            System.out.println("Successfully funded sender account: " + sender.getWalletAddress());
        } catch (Exception e) {
            System.err.println("Failed to fund sender account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String transfer(Wallet sender, Wallet recipient) {
        try {
            Credentials senderCredentials = loadCredentials(sender.getWalletFileName(), sender.getWalletPassword());

            MyCashToken contract = MyCashToken.load(CONTRACT_ADDRESS, web3j, new RawTransactionManager(web3j, senderCredentials, CHAIN_ID), GAS_PROVIDER);

            BigInteger transferAmount = BigInteger.valueOf(500);

            // Check initial balances
            BigInteger initialSenderBalance = contract.balanceOf(sender.getWalletAddress()).send();
            BigInteger initialRecipientBalance = contract.balanceOf(recipient.getWalletAddress()).send();
            System.out.println("Initial Balance of Sender: " + initialSenderBalance);
            System.out.println("Initial Balance of Recipient: " + initialRecipientBalance);

            // Transfer ERC20 tokens
            String transactionHash = transferTokens(senderCredentials, CONTRACT_ADDRESS, recipient.getWalletAddress(), transferAmount);

            // Check updated balances
            BigInteger updatedSenderBalance = contract.balanceOf(sender.getWalletAddress()).send();
            BigInteger updatedRecipientBalance = contract.balanceOf(recipient.getWalletAddress()).send();

            System.out.println("Updated Balance of Sender after Transfer: " + updatedSenderBalance);
            System.out.println("Updated Balance of Recipient after Transfer: " + updatedRecipientBalance);

            return transactionHash;
        } catch (Exception e) {
            System.err.println("Failed to transfer tokens: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}