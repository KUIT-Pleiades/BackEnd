package com.pleiades.controller;

import com.pleiades.dto.NaverLoginRequest;
import com.pleiades.dto.NaverLoginResponse;
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

        String codeOrToken = loginRequest.getCode();
        String type = loginRequest.getType();

        if (codeOrToken == null || type == null) {
            log.error("에러: code || type -> NULL");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new NaverLoginResponse("Invalid request", null));
        }

        log.info("네이버 로그인 request 받음. Type: {}, Code/Token: {}", type, codeOrToken);

        if ("Auth".equalsIgnoreCase(type)) {
            log.info("Auth: 인가 코드로 네이버 로그인 진행");
            return naverLoginService.handleNaverLoginCallback(codeOrToken, loginRequest.getState());
        } else if ("Refresh".equalsIgnoreCase(type)) {
            log.info("Refresh: 앱 자체 refresh token으로 로그인 진행");
            NaverLoginResponse userInfo = naverLoginService.handleRefreshTokenLogin(codeOrToken);
            return ResponseEntity.ok(userInfo);
        } else {
            log.error("에러: 로그인 타입 매치 실패 - {}", type);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ExceptionHandler(NaverRefreshTokenExpiredException.class)
    public ResponseEntity<Void> handleRefreshTokenExpired(HttpServletResponse response) throws IOException {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
    }
}