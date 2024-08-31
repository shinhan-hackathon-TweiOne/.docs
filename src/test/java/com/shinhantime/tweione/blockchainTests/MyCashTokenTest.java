package com.shinhantime.tweione.blockchainTests;

import com.shinhantime.tweione.MyCashToken;


import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.junit.jupiter.api.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
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
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MyCashTokenTest {
    private static Web3j web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));
    private static final String WALLET_DIRECTORY = "C:/Users/SSAFY/Blockchain/keystore";
    private static final String CONTRACT_ADDRESS = "0xdf956fd4164999c30a064e88de74607d57da1e9a";

    // 예시 키스토어 파일명 (여러 계정)
    private static final String SENDER_KEYSTORE = "UTC--2024-08-30T11-42-32.55629000Z--c094831460fc0177601f67aa7bcee65e4bf2d83c.json";
    private static final String RECIPIENT_ADDRESS = "0x530986cf8c74ef308ed5dd541cc52f1393c2bb62"; // 받는 사람 주소

    // 보내는 사람 정보
    private static final String SENDER_PASSWORD = "ssafy";
    private static final String SENDER_ADDRESS = "0xc094831460fc0177601f67aa7bcee65e4bf2d83c"; // 보내는 사람 주소

    // 관리자 계정 정보
    private static final String RELAY_KEYSTORE = "UTC--2024-08-29T08-15-21.922424300Z--41e4cf7b9f52f76f74e486337cc5164509ea9f0f.json";
    private static final String RELAY_PASSWORD = "ssafy";
    private static final String ADMIN_ADDRESS = "0x41e4cf7b9f52f76f74e486337cc5164509ea9f0f"; // 관리자 주소

    // 가스 가격 및 한도 설정
    private static BigInteger gasPrice = BigInteger.valueOf(50000000000L); // 50 Gwei
    private static BigInteger gasLimit = BigInteger.valueOf(8000000L); // 8,000,000 가스 한도
    private static ContractGasProvider GAS_PROVIDER = new StaticGasProvider(gasPrice, gasLimit);

    // EIP-155 체인 ID
    private static final long CHAIN_ID = 1234; // 사용중인 네트워크의 체인 ID로 변경해야 함

    private Credentials loadCredentials(String keystoreFilename, String password) throws Exception {
        File keystoreFile = new File(WALLET_DIRECTORY, keystoreFilename);
        return WalletUtils.loadCredentials(password, keystoreFile);
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
                gasPrice, // Gas price
                gasLimit, // Gas limit
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

    private void transferTokens(Credentials credentials, String contractAddress, String recipientAddress, BigInteger amount) throws Exception {
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
                gasPrice,
                gasLimit,
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
    }

    @Test
    public void testChargeAndCheckBalance() throws Exception {
        Credentials adminCredentials = loadCredentials(RELAY_KEYSTORE, RELAY_PASSWORD);
        MyCashToken contract = MyCashToken.load(CONTRACT_ADDRESS, web3j, new RawTransactionManager(web3j, adminCredentials, CHAIN_ID), GAS_PROVIDER);

        BigInteger chargeAmount = BigInteger.valueOf(1000);

        // 충전 전 잔액 조회
        BigInteger initialBalance = contract.balanceOf(SENDER_ADDRESS).send();
        System.out.println("Initial Balance of Sender: " + initialBalance);

        // 충전
        TransactionReceipt receipt = contract.charge(SENDER_ADDRESS, chargeAmount).send();
        assertNotNull(receipt);

        // 충전 후 잔액 조회
        BigInteger newBalance = contract.balanceOf(SENDER_ADDRESS).send();
        assertEquals(initialBalance.add(chargeAmount), newBalance);
        System.out.println("New Balance of Sender after Charge: " + newBalance);
    }

    @Test
    public void fundSenderAccount() throws Exception {
        Credentials funderCredentials = loadCredentials(RELAY_KEYSTORE, RELAY_PASSWORD);
        Credentials senderCredentials = loadCredentials(SENDER_KEYSTORE, SENDER_PASSWORD);

        // 전송할 이더 양
        BigInteger amount = BigInteger.valueOf(1_000_000_000_000_000_000L); // 1 ETH in Wei

        sendEther(funderCredentials, senderCredentials.getAddress(), amount);
    }

    @Test
    public void testTransferAndCheckBalanceWithoutMetaTransaction() throws Exception {
        Credentials senderCredentials = loadCredentials(SENDER_KEYSTORE, SENDER_PASSWORD);

        MyCashToken contract = MyCashToken.load(CONTRACT_ADDRESS, web3j, new RawTransactionManager(web3j, senderCredentials, CHAIN_ID), GAS_PROVIDER);

        BigInteger transferAmount = BigInteger.valueOf(500);

        // Check initial balances
        BigInteger initialSenderBalance = contract.balanceOf(SENDER_ADDRESS).send();
        BigInteger initialRecipientBalance = contract.balanceOf(RECIPIENT_ADDRESS).send();
        System.out.println("Initial Balance of Sender: " + initialSenderBalance);
        System.out.println("Initial Balance of Recipient: " + initialRecipientBalance);

        // Transfer ERC20 tokens
        transferTokens(senderCredentials, CONTRACT_ADDRESS, RECIPIENT_ADDRESS, transferAmount);

        // Check updated balances
        BigInteger updatedSenderBalance = contract.balanceOf(SENDER_ADDRESS).send();
        BigInteger updatedRecipientBalance = contract.balanceOf(RECIPIENT_ADDRESS).send();

        // Validate balances
        assertEquals(initialSenderBalance.subtract(transferAmount), updatedSenderBalance);
        assertEquals(initialRecipientBalance.add(transferAmount), updatedRecipientBalance);

        System.out.println("Updated Balance of Sender after Transfer: " + updatedSenderBalance);
        System.out.println("Updated Balance of Recipient after Transfer: " + updatedRecipientBalance);
    }

}