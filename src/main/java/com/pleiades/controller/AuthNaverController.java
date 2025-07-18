package com.pleiades.controller;

import com.pleiades.dto.LoginResponseDto;
import com.pleiades.dto.naver.NaverLoginRequestDto;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.exception.NaverRefreshTokenExpiredException;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.auth.NaverLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Tag(name = "Auth: Naver", description = "네이버 인증 관련 API")
@Slf4j
@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class AuthNaverController {

    private final NaverLoginService naverLoginService;
    private final AuthService authService;

    @Operation(summary = "", description = "")
    @PostMapping("/naver")
    public ResponseEntity<Map<String,String>> handleNaverLogin(@RequestBody NaverLoginRequestDto loginRequest, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        log.info("handleNaverLogin 시작");

        String authCode = loginRequest.getCode();

        if (authCode == null) {
            log.error("에러: authCode -> NULL");
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
        LoginResponseDto loginResponse = naverLoginService.handleNaverLoginCallback(authCode);
        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();

        authService.addRefreshTokenCookie(response, refreshToken);
        long endTime = System.currentTimeMillis();
        double elapsedTime = (endTime - startTime) / 1000.0;
        log.info("Naver Login 수행 시간: {} ms",  String.format("%.1f", elapsedTime));
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