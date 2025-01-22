package com.pleiades.controller;

import com.pleiades.dto.naver.LoginCBResponse;
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

    @GetMapping("")
    public String testRequest(@RequestParam("code") String code) {
        log.info("요청 도착: {}", code);
        System.out.println("요청 시스템 프린트");
        return "요청 성공";
    }

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
            LoginCBResponse response = naverLoginService.handleNaverLoginCallback(codeOrToken, loginRequest.getState());
            return ResponseEntity.ok(response);
        }
//        else if ("Refresh".equalsIgnoreCase(type)) {
//            log.info("Refresh: 앱 자체 refresh token으로 로그인 진행");
//            LoginCBResponse response = new LoginCBResponse();
//            return ResponseEntity.ok(response);
//        }
        else {
            log.error("에러: 로그인 타입 매치 실패 - {}", type);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ExceptionHandler(NaverRefreshTokenExpiredException.class)
    public ResponseEntity<Void> handleRefreshTokenExpired(HttpServletResponse response) throws IOException {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
    }
}