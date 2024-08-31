package com.shinhantime.tweione.blockchainTests;

import com.shinhantime.tweione.MyCashToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.File;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DeployContractTest {

    @Test
    public void testDeployContract() throws Exception {
        // 1. Web3j 인스턴스 생성
        Web3j web3j = Web3j.build(new HttpService("http://127.0.0.1:8545")); // 로컬 이더리움 노드에 연결

        // 2. 계정 자격증명 로드 (키스토어 파일)
        String keystorePath = "C:/Users/SSAFY/Blockchain/keystore/UTC--2024-08-29T08-15-21.922424300Z--41e4cf7b9f52f76f74e486337cc5164509ea9f0f.json";
        String password = "ssafy";
        Credentials credentials = WalletUtils.loadCredentials(password, new File(keystorePath));

        // 3. 가스 가격과 가스 한도 명시적 설정
        BigInteger gasPrice = BigInteger.valueOf(50000000000L); // 50 Gwei
        BigInteger gasLimit = BigInteger.valueOf(6000000L);     // 600만 가스 한도
        ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

        // 4. 체인 ID 설정 (예: 메인넷은 1, Ropsten은 3, Kovan은 42 등)
        long chainId = 1234; // 여기서 1234는 예시 체인 ID입니다. 실제 사용하는 네트워크의 체인 ID를 사용하세요.

        // 5. RawTransactionManager 생성하여 EIP-155 적용
        RawTransactionManager transactionManager = new RawTransactionManager(web3j, credentials, chainId);

        // 6. 스마트 컨트랙트 배포
        MyCashToken contract = MyCashToken.deploy(
                web3j, transactionManager, gasProvider, BigInteger.valueOf(1000000)
        ).send();

        if (contract.getTransactionReceipt().isPresent()) {
            // 7. 트랜잭션 해시 가져오기
            String transactionHash = contract.getTransactionReceipt().get().getTransactionHash();
            System.out.println("transactionHash : " + transactionHash);

            // 8. 트랜잭션 영수증 대기
            TransactionReceipt transactionReceipt = waitForTransactionReceipt(web3j, transactionHash);

            // 9. 배포된 스마트 컨트랙트 주소 검증
            String contractAddress = transactionReceipt.getContractAddress();
            System.out.println("Contract deployed at address: " + contractAddress);

            // 배포된 컨트랙트 주소가 null이 아닌지 확인
            assertNotNull(contractAddress);
        } else {
            System.err.println("Contract deployment failed: No transaction receipt");
        }
    }

    private TransactionReceipt waitForTransactionReceipt(Web3j web3j, String transactionHash) throws Exception {
        int attempts = 40; // 최대 시도 횟수
        int sleepDuration = 1000; // 대기 시간 (밀리초)

        for (int i = 0; i < attempts; i++) {
            Optional<TransactionReceipt> transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt();
            if (transactionReceipt.isPresent()) {
                return transactionReceipt.get();
            }
            Thread.sleep(sleepDuration); // 대기 후 재시도
        }

        throw new RuntimeException("Transaction receipt not generated after " + attempts + " attempts");
    }



}
