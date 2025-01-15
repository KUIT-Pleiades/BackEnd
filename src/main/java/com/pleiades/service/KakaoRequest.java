package com.pleiades.service;

import com.pleiades.dto.kakao.KakaoAccessTokenDto;
import com.pleiades.dto.kakao.KakaoTokenDto;
import com.pleiades.dto.kakao.KakaoUserDto;
import com.pleiades.strings.KakaoUrl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class KakaoRequest {
    // RestTemplate 객체 생성
    static RestTemplate restTemplate = new RestTemplate();

    public static KakaoTokenDto postAccessToken(String code) {
        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  // application/x-www-form-urlencoded;charset=utf-8

        // 요청 본문
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", KakaoUrl.KAKAO_CLIENT_ID.getUrl());
        body.add("redirect_uri", KakaoUrl.REDIRECT_URI.getUrl());
        body.add("code", code);

        // 요청 객체
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        log.info("2ed");
//        try {
            // POST 요청
            ResponseEntity<KakaoTokenDto> response = restTemplate.postForEntity(KakaoUrl.TOKEN_URL.getUrl(), entity, KakaoTokenDto.class);
            log.info("3ed");
            return response.getBody();
//
//        } catch (Exception e) {
//            log.info("4ed");
//            return null;
//        }
    }

    public static KakaoUserDto postUserEmail(String token) {
        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(token);

        // 요청 본문
        Map<String, Object> body = new HashMap<>();
        body.put("property_keys", new String[]{"kakao_account.email"});

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // POST 요청
            ResponseEntity<KakaoUserDto> response = restTemplate.postForEntity(KakaoUrl.USER_INFO_URL.getUrl(), entity, KakaoUserDto.class);

            return response.getBody();

        } catch (Exception e) {
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
        body.put("client_id", KakaoUrl.KAKAO_CLIENT_ID.getUrl());
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
