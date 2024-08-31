package com.shinhantime.tweione.blockchainTests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreateAndGetAccountTest {
    private static Web3j web3j;
    private static final String WALLET_DIRECTORY = "C:/Users/SSAFY/Blockchain/keystore"; // 지갑 파일 저장 디렉토리
    private static final String PASSWORD = "ssafy";

    @BeforeAll
    public static void setUp() {
        // Web3j 인스턴스를 생성하여 로컬 이더리움 노드에 연결합니다.
        web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));
    }

    @Test
    public void testConnection() throws Exception {
        assertNotNull(web3j.web3ClientVersion().send().getWeb3ClientVersion());
    }


    @Test
    public void testCreateAccount() throws Exception {
        // 새로운 계정을 생성합니다.
        String walletFileName = WalletUtils.generateNewWalletFile(PASSWORD, new File(WALLET_DIRECTORY));
        assertNotNull(walletFileName);

        // 생성된 계정의 Credentials를 로드합니다.
        Credentials credentials = WalletUtils.loadCredentials(PASSWORD, WALLET_DIRECTORY + "/" + walletFileName);
        assertNotNull(credentials);
        assertNotNull(credentials.getAddress());

        System.out.println("New account created: " + credentials.getAddress());
    }

    @Test
    public void testListAccounts() throws Exception {
        // 네트워크에서 모든 계정을 가져와 출력합니다.
        EthAccounts accounts = web3j.ethAccounts().send();
        List<String> accountList = accounts.getAccounts();
        assertNotNull(accountList);
        assertFalse(accountList.isEmpty());

        System.out.println("Accounts in the network:");
        for (String account : accountList) {
            System.out.println(account);
        }
    }
}
