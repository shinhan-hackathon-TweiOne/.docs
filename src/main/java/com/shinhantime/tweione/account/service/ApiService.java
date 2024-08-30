package com.shinhantime.tweione.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhantime.tweione.account.repository.dto.AuthRequest;
import com.shinhantime.tweione.account.repository.dto.Header;
import com.shinhantime.tweione.account.repository.dto.VerifyRequest;
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

    public Map<String, Object> sendAuth(String accountNumber) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

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
                .userKey("c09cacfa-976e-4c6b-975d-b8b0a4b3785c")
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

    public Map<String, Object> verifyAuth(String accountNumber, String authCode) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String institutionTransactionUniqueNo = generateUniqueNo();

            Header header = Header.builder()
                    .apiName("checkAuthCode")
                    .transmissionDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .transmissionTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")))
                    .institutionCode("00100")
                    .fintechAppNo("001")
                    .apiServiceCode("checkAuthCode")
                    .institutionTransactionUniqueNo(institutionTransactionUniqueNo)
                    .apiKey("8565abfe5b5a47088dfc0686676dadd2")
                    .userKey("c09cacfa-976e-4c6b-975d-b8b0a4b3785c")
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

    private static String generateUniqueNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String randomDigits = String.format("%06d", (int) (Math.random() * 1000000)); // 6자리 랜덤 숫자 생성
        return timestamp + randomDigits;
    }
}
