package com.shinhantime.tweione.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhantime.tweione.User.repository.UserEntity;
import com.shinhantime.tweione.User.repository.UserRepository;
import com.shinhantime.tweione.User.service.UserService;
import com.shinhantime.tweione.UserKeyEntity.Repository.UserKeyEntity;
import com.shinhantime.tweione.UserKeyEntity.Repository.UserKeyRepository;
import com.shinhantime.tweione.account.repository.AccountRepository;
import com.shinhantime.tweione.account.repository.dto.AuthRequest;
import com.shinhantime.tweione.account.repository.dto.Header;
import com.shinhantime.tweione.account.repository.dto.VerifyRequest;
import com.shinhantime.tweione.account.repository.dto.sendDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class ApiService {
    private final UserKeyRepository userKeyRepository;
    private final UserRepository userRepository;

    public ApiService(UserKeyRepository userKeyRepository, UserRepository userRepository) {
        this.userKeyRepository = userKeyRepository;
        this.userRepository = userRepository;
    }
    public Map<String, Object> sendAuth(String accountNumber, Long userId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        UserKeyEntity userKeyEntity = userKeyRepository.findById(userId).orElse(null);

        String institutionTransactionUniqueNo = generateUniqueNo();
        Header header = Header.builder()
                .apiName("openAccountAuth")
                .transmissionDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .transmissionTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")))
                .institutionCode("00100")
                .fintechAppNo("001")
                .apiServiceCode("openAccountAuth")
                .institutionTransactionUniqueNo(institutionTransactionUniqueNo)
                .apiKey("8565abfe5b5a47088dfc0686676dadd2")
                .userKey(userKeyEntity.getUserKey())
                .build();

        // Update authText to match Postman request
        AuthRequest requestBody = AuthRequest.builder()
                .header(header)
                .accountNo(accountNumber)
                .authText("SSAFY") // Update to match Postman
                .build();

        // Log the request body
        System.out.println("Request Body: " + new ObjectMapper().writeValueAsString(requestBody));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthRequest> entity = new HttpEntity<>(requestBody, headers);

        String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/accountAuth/openAccountAuth";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), Map.class);
    }

    public Map<String, Object> verifyAuth(String accountNumber, String authCode, Long userId) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String institutionTransactionUniqueNo = generateUniqueNo();

            UserKeyEntity userKeyEntity = userKeyRepository.findById(userId).orElse(null);

            Header header = Header.builder()
                    .apiName("checkAuthCode")
                    .transmissionDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .transmissionTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")))
                    .institutionCode("00100")
                    .fintechAppNo("001")
                    .apiServiceCode("checkAuthCode")
                    .institutionTransactionUniqueNo(institutionTransactionUniqueNo)
                    .apiKey("8565abfe5b5a47088dfc0686676dadd2")
                    .userKey(userKeyEntity.getUserKey())
                    .build();

            VerifyRequest requestBody = VerifyRequest.builder()
                    .header(header)
                    .accountNo(accountNumber)
                    .authText("SSAFY")
                    .authCode(authCode)
                    .build();

            HttpHeaders headers = new HttpHeaders();

            HttpEntity<VerifyRequest> entity = new HttpEntity<>(requestBody, headers);

            String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/accountAuth/checkAuthCode";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), Map.class);

        } catch (HttpServerErrorException e) {
            // 서버 오류 처리
            System.err.println("Server Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("API 서버에서 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (RestClientException e) {
            // 클라이언트 오류 처리
            System.err.println("Client Error: " + e.getMessage());
            throw new RuntimeException("API 요청 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> recharge( String balance, Long userId) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String institutionTransactionUniqueNo = generateUniqueNo();

            UserKeyEntity userKeyEntity = userKeyRepository.findById(userId).orElse(null);
            UserEntity userEntity = userRepository.findById(userId).orElse(null);

            Header header = Header.builder()
                    .apiName("updateDemandDepositAccountTransfer")
                    .transmissionDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .transmissionTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")))
                    .institutionCode("00100")
                    .fintechAppNo("001")
                    .apiServiceCode("updateDemandDepositAccountTransfer")
                    .institutionTransactionUniqueNo(institutionTransactionUniqueNo)
                    .apiKey("8565abfe5b5a47088dfc0686676dadd2")
                    .userKey(userKeyEntity.getUserKey())
                    .build();

            sendDTO requestBody = sendDTO.builder()
                    .header(header)
                    .depositAccountNo("0883502918143683")
                    .depositTransactionSummary("(수시입출금): 입금(이체)")
                    .transactionBalance(balance)
                    .withdrawalAccountNo(userEntity.getMainAccount().getAccountNumber())
                    .withdrawalTransactionSummary("(수시입출금): 출금(이체)")
                    .build();

            HttpHeaders headers = new HttpHeaders();

            HttpEntity<sendDTO> entity = new HttpEntity<>(requestBody, headers);

            String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/updateDemandDepositAccountTransfer";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            Long curMoney = userEntity.getCurrentMoney();

            userEntity.setCurrentMoney(curMoney  + Long.parseLong(balance));
            userRepository.save(userEntity);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), Map.class);

        } catch (HttpServerErrorException e) {
            // 서버 오류 처리
            System.err.println("Server Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("API 서버에서 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (RestClientException e) {
            // 클라이언트 오류 처리
            System.err.println("Client Error: " + e.getMessage());
            throw new RuntimeException("API 요청 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private static String generateUniqueNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String randomDigits = String.format("%06d", (int) (Math.random() * 1000000)); // 6자리 랜덤 숫자 생성
        return timestamp + randomDigits;
    }
}
