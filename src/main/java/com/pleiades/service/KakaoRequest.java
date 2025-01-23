package com.pleiades.service;

import com.pleiades.dto.kakao.KakaoAccessTokenDto;
import com.pleiades.dto.kakao.KakaoTokenDto;
import com.pleiades.dto.kakao.KakaoUserDto;
import com.pleiades.strings.KakaoUrl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class KakaoRequest {
    // RestTemplate 객체 생성
    static RestTemplate restTemplate = new RestTemplate();

    @Value("${KAKAO_CLIENT_ID}")
    private static String KAKAO_CLIENT_ID;

    public static KakaoTokenDto postAccessToken(String code) {
        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "x-www-form-urlencoded", StandardCharsets.UTF_8));  // application/x-www-form-urlencoded;charset=utf-8

        // 요청 본문
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("redirect_uri", KakaoUrl.REDIRECT_URI.getUrl());
        body.add("code", code);

        // 요청 객체
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        log.info("2ed");
        try {   // POST 요청
            ResponseEntity<KakaoTokenDto> response = restTemplate.postForEntity(KakaoUrl.TOKEN_URL.getUrl(), entity, KakaoTokenDto.class);
            log.info("3ed");
            return response.getBody();
        } catch (Exception e) {
            log.info("4ed");
            log.error(e.getMessage());
            return null;
        }
    }

    public static KakaoUserDto postUserInfo(String token) {
        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);      // body를 MultiValueMap 형식으로 전달해야함
        headers.setBearerAuth(token);

        // 요청 본문
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.email\"]");    // body.add("property_keys", "kakao_account.email")로 했는데 안 됨 body.post("property_keys", List형식)도 안 됨.

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            // POST 요청
            log.info("postUserInfo");
            ResponseEntity<KakaoUserDto> response = restTemplate.postForEntity(KakaoUrl.USER_INFO_URL.getUrl(), entity, KakaoUserDto.class);

            return response.getBody();

        } catch (Exception e) {
            System.err.println("error message: " + e.getMessage());
            return null;
        }
    }

    public static KakaoAccessTokenDto getAccessTokenInfo(String token) {
        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(headers);

        try {
            // GET 요청
            ResponseEntity<KakaoAccessTokenDto> response = restTemplate.exchange(KakaoUrl.ACCESS_TOKEN_INFO_URL.getUrl(), HttpMethod.GET, entity, KakaoAccessTokenDto.class);

            return response.getBody();

        } catch (Exception e) {
            return null;
        }
    }

    public static KakaoTokenDto postRefreshAccessToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, Object> body = new HashMap<>();
        body.put("grant_type", new String[]{"refresh_token"});
        body.put("client_id", KAKAO_CLIENT_ID);
        body.put("refresh_token", token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // POST 요청
            ResponseEntity<KakaoTokenDto> response = restTemplate.postForEntity(KakaoUrl.USER_INFO_URL.getUrl(), entity, KakaoTokenDto.class);

            return response.getBody();

        } catch (Exception e) {
            return null;
        }
    }
}