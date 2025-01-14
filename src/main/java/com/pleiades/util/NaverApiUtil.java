package com.pleiades.util;

import com.pleiades.dto.NaverLoginResponse;
import com.pleiades.entity.NaverToken;
import com.pleiades.entity.User;
import com.pleiades.exception.NaverRefreshTokenExpiredException;
import com.pleiades.repository.NaverTokenRepository;
import com.pleiades.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class NaverApiUtil {

    private final String clientId = System.getenv("NAVER_CLIENT_ID");
    private final String clientSecret = System.getenv("NAVER_CLIENT_SECRET");

    private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 3600000; // 1시간 (3600초)

    public Map<String,String> getTokens(String code, String state) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

        if (response.getBody() != null) {
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", (String) response.getBody().get("access_token"));
            tokens.put("refresh_token", (String) response.getBody().get("refresh_token"));
            tokens.put("expires_in", (String) response.getBody().get("expires_in"));
            tokens.put("token_type", (String) response.getBody().get("token_type"));
            return tokens;
        } else {
            log.error("에러 - 네이버 토큰 받아 오기 실패 : {}", response.getBody());
            throw new IllegalStateException("에러 - 네이버 토큰 요청 실패");
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
            throw new IllegalStateException("네이버 access token 갱신 실패");
        }
        return (String) response.getBody().get("access_token");
    }

    public String getValidAccessToken(NaverToken naverToken) {
        if (isAccessTokenExpired(naverToken.getLastUpdated())) {
            String newAccessToken = refreshAccessToken(naverToken.getRefreshToken());
            if (newAccessToken == null) {
                throw new NaverRefreshTokenExpiredException("네이버 Refresh Token 만료 - 재로그인 필요");
            }
            return newAccessToken;
        }
        return naverToken.getAccessToken();
    }

    private boolean isAccessTokenExpired(long lastUpdatedTimestamp) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastUpdatedTimestamp) >= ACCESS_TOKEN_EXPIRATION_TIME;
    }


    public NaverLoginResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, request, Map.class);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("response");

        return new NaverLoginResponse(
                (String) responseBody.get("email"),
                accessToken
        );
    }
}
