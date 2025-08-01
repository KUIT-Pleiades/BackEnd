package com.pleiades.util;

import com.pleiades.dto.naver.NaverLoginResponseDto;
import com.pleiades.entity.NaverToken;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.exception.NaverRefreshTokenExpiredException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverApiUtil {

    private final String clientId = System.getenv("NAVER_CLIENT_ID");
    private final String clientSecret = System.getenv("NAVER_CLIENT_SECRET");

    private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";

    private final RestTemplate restTemplate;

    public NaverApiUtil() {
        this.restTemplate = createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            // 둘 다 SocketTimeoutException
            factory.setConnectTimeout(2000); // 연결 timeout
            factory.setReadTimeout(2000);    // 응답 대기 timeout
            return new RestTemplate(factory);
    }

    public String generateEncodedState() {
        String state = UUID.randomUUID().toString();
        return URLEncoder.encode(state, StandardCharsets.UTF_8);
    }

    public Map<String,String> getTokens(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String state = generateEncodedState();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        log.info("code: {}", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        dummyGetRequest();

        int maxRetries = 3; // 최대 재시도 횟수
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);
                log.info("Naver API Util Response: {}", response);
                if (response.getBody() != null) {
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("access_token", (String) response.getBody().get("access_token"));
                    tokens.put("refresh_token", (String) response.getBody().get("refresh_token"));
                    tokens.put("expires_in", (String) response.getBody().get("expires_in"));
                    tokens.put("token_type", (String) response.getBody().get("token_type"));
                    return tokens;
                } else {
                    log.error("에러 - 네이버 토큰 받아 오기 실패 : {}", response.getBody());
                    throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
                }
            } catch (Exception e) {
                if (e.getCause() instanceof SocketTimeoutException ||
                        e.getMessage().contains("Connection reset") ||
                        e.getMessage().contains("I/O error")) {
                    log.info("네이버 API 요청 메시지: {}", e.getMessage());
                    log.info("네이버 API 요청 Timeout 발생 -> {}번째 재시도", attempt + 1);
                    attempt++;
//                    try { // 추후 network 부하 고려
//                        Thread.sleep(500); // 대기 후 재시도
//                    } catch (InterruptedException interruptedException) {
//                        Thread.currentThread().interrupt();
//                        throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
//                    }
                } else {
                    log.error("네이버 API 요청 중 오류 발생: {}", e.getMessage());
                    throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
                }
            }
        }
        log.error("네이버 API 요청 {}번 실패-> 중단", maxRetries);
        throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }
    private void dummyGetRequest() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(TOKEN_URL, String.class);
            log.info("네이버 GET Request 성공: {}", response.getStatusCode());
        } catch (Exception e) {
            if (e.getCause() instanceof SocketTimeoutException ||
                    e.getMessage().contains("Connection reset")) {
                log.error("네이버 GET Request 실패: {}", e.getMessage());
            } else {
                log.error("네이버 GET 오류 : {}", e.getMessage());
                throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
            }
        }
    }

    public String refreshAccessToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

        log.info("토큰 갱신 - Refresh Response: {}", response.getBody());

        if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
            log.error("에러 - access token 갱신 실패 : {}", response.getBody());
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
            // throw new NaverRefreshTokenExpiredException("네이버 access token 갱신 실패");
        }
        return (String) response.getBody().get("access_token");
    }

    public String getValidAccessToken(NaverToken naverToken) {
        if (naverToken.getAccessToken() == null) {
            String newAccessToken = refreshAccessToken(naverToken.getRefreshToken());
            if (newAccessToken == null) {
                log.info("네이버 Refresh Token 만료 - 재로그인 필요");
                throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
                //throw new NaverRefreshTokenExpiredException("네이버 Refresh Token 만료 - 재로그인 필요");
            }
            return newAccessToken;
        }
        return naverToken.getAccessToken();
    }

    public NaverLoginResponseDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, request, Map.class);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("response");
        String email = responseBody.get("email").toString();
        log.info("네이버 로그인 getUserInfo - email: {}", email);
        return new NaverLoginResponseDto(
                email,
                accessToken
        );
    }
}