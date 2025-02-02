package com.pleiades.controller;

import com.pleiades.dto.LoginCBResponse;
import com.pleiades.dto.naver.NaverLoginRequest;
import com.pleiades.dto.naver.NaverLoginResponse;
import com.pleiades.exception.NaverRefreshTokenExpiredException;
import com.pleiades.service.NaverLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor

public class AuthNaverController {

    private final NaverLoginService naverLoginService;

    @PostMapping("/naver")
    public ResponseEntity<?> handleNaverLogin(@RequestBody NaverLoginRequest loginRequest, HttpServletResponse response) {
        log.info("handleNaverLogin 시작");

        String authCode = loginRequest.getCode();

        if (authCode == null) {
            log.error("에러: authCode -> NULL");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        log.info("네이버 로그인 request 받음. AuthCode: {}", authCode);
        LoginCBResponse loginResponse = naverLoginService.handleNaverLoginCallback(authCode);
        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();

        response.setHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(refreshToken));

        return ResponseEntity.ok(accessToken);
    }

    private String createRefreshTokenCookie(String refreshToken) {
        int maxAge = 7 * 24 * 60 * 60;
        boolean secure = false;
        return String.format("refreshToken=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax; Secure=%s",
                refreshToken, maxAge, secure ? "true" : "false");
    }
    @ExceptionHandler(NaverRefreshTokenExpiredException.class)
    public ResponseEntity<Void> handleRefreshTokenExpired(HttpServletResponse response) throws IOException {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
    }
}