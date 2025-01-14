package com.pleiades.controller;

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
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final NaverLoginService naverLoginService;

    private final String clientId = System.getenv("NAVER_CLIENT_ID");
    //private String redirectUri = "http://54.252.108.194:80/auth/login/naver/callback";
    private String redirectUri = "http://localhost:8080/auth/login/naver/callback";

    private String state; // CSRF 방지용 임의 값

    private void naverLogin(HttpServletResponse response) throws IOException {
        String naverLoginUrl = String.format(
                "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&state=%s&prompt=login",
                clientId, redirectUri, state
        );
        response.sendRedirect(naverLoginUrl);
    }

    @ExceptionHandler(NaverRefreshTokenExpiredException.class)
    public ResponseEntity<Void> handleRefreshTokenExpired(HttpServletResponse response) throws IOException {
        naverLogin(response);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @GetMapping("/login/naver")
    public ResponseEntity<Void> redirectToNaverLogin(HttpServletResponse response) throws IOException {
        naverLogin(response);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @GetMapping("/login/naver/callback")
    public ResponseEntity<NaverLoginResponse> naverLoginCallback(
            @RequestParam String code,
            @RequestParam String state) {

        // 네이버로부터 Access Token 가져오기
        NaverLoginResponse userInfo = naverLoginService.handleNaverLoginCallback(code, state);

        return ResponseEntity.ok(userInfo);
    }
}
