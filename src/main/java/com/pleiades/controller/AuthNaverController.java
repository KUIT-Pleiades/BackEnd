package com.pleiades.controller;

import com.pleiades.dto.LoginResponseDto;
import com.pleiades.dto.naver.NaverLoginRequestDto;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.exception.NaverRefreshTokenExpiredException;
import com.pleiades.service.AuthService;
import com.pleiades.service.NaverLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class AuthNaverController {

    private final AuthService authService;
    private final NaverLoginService naverLoginService;

    @PostMapping("/naver")
    public ResponseEntity<Map<String,String>> handleNaverLogin(@RequestBody NaverLoginRequestDto loginRequest, HttpServletResponse response) {
        log.info("handleNaverLogin 시작");

        String authCode = loginRequest.getCode();

        if (authCode == null) {
            log.error("에러: authCode -> NULL");
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
        log.info("네이버 로그인 request 받음. AuthCode: {}", authCode);
        LoginResponseDto loginResponse = naverLoginService.handleNaverLoginCallback(authCode);
        String accessToken = loginResponse.getAccessToken();
//        String refreshToken = loginResponse.getRefreshToken();

//        authService.addRefreshTokenCookie(response, refreshToken);

        log.info("네이버 로그인 access token: {}", accessToken);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(Map.of("accessToken", accessToken));
    }

    @ExceptionHandler(NaverRefreshTokenExpiredException.class)
    public ResponseEntity<Void> handleRefreshTokenExpired(HttpServletResponse response) throws IOException {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
    }
}