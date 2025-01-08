package com.pleiades.util;

import com.pleiades.dto.NaverLoginResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class NaverApiUtil {

    private String clientId = "Wz8BBhseiVn4cphvBlrS";
    private String clientSecret = "FrvbVSXmKK";

    private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";

    public String getAccessToken(String code, String state) {
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

        return (String) response.getBody().get("access_token");
    }

    public NaverLoginResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, request, Map.class);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("response");

        log.info("네이버 로그인 응답 response USERNAME: {}\n", responseBody.get("name"));

        return new NaverLoginResponse(
                (String) responseBody.get("name"),
                (String) responseBody.get("id"),
                //(String) responseBody.get("email"),
                (String) responseBody.get("birthday")
        );
    }
}
