package com.pleiades.service;

import com.pleiades.dto.KakaoTokenDto;
import com.pleiades.dto.KakaoUserDto;
import com.pleiades.strings.KakaoUrl;
import lombok.Getter;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Getter
public class KakaoRequest {
    // RestTemplate 객체 생성
    static RestTemplate restTemplate = new RestTemplate();

    public static ResponseEntity<KakaoTokenDto> postAccessToken(String code) {
        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  // application/x-www-form-urlencoded;charset=utf-8

        // 요청 본문
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("client_id", KakaoUrl.KAKAO_CLIENT_ID.getUrl());
        body.put("redirect_uri", KakaoUrl.REDIRECT_URI.getUrl());
        body.put("code", code);

        // 요청 객체
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            // POST 요청
            ResponseEntity<KakaoTokenDto> response = restTemplate.postForEntity(KakaoUrl.TOKEN_URL.getUrl(), entity, KakaoTokenDto.class);

            return response;

        } catch (Exception e) {
            return null;
        }
    }

    public static ResponseEntity<KakaoUserDto> postUserEmail(String token) {
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

            return response;

        } catch (Exception e) {
            return null;
        }
    }

}
