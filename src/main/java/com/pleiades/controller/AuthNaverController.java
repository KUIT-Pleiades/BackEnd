package com.pleiades.controller;

import com.pleiades.dto.LoginCBResponse;
import com.pleiades.dto.naver.NaverLoginRequest;
import com.pleiades.dto.naver.NaverLoginResponse;
import com.pleiades.exception.NaverRefreshTokenExpiredException;
import com.pleiades.service.NaverLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/auth/login/naver")
@RequiredArgsConstructor

public class AuthNaverController {

    private final NaverLoginService naverLoginService;

    @PostMapping("/callback")
    public ResponseEntity<?> handleNaverLogin(@RequestBody NaverLoginRequest loginRequest) {
        log.info("handleNaverLogin 시작");

        String authCode = loginRequest.getCode();

        if (authCode == null) {
            log.error("에러: authCode -> NULL");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NaverLoginResponse("Invalid request", null));
        }
        log.info("네이버 로그인 request 받음. AuthCode: {}", authCode);
        LoginCBResponse response = naverLoginService.handleNaverLoginCallback(authCode);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(NaverRefreshTokenExpiredException.class)
    public ResponseEntity<Void> handleRefreshTokenExpired(HttpServletResponse response) throws IOException {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
    }
}