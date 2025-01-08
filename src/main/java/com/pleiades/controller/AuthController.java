package com.pleiades.controller;

import com.pleiades.dto.NaverLoginResponse;
import com.pleiades.service.NaverLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final NaverLoginService naverLoginService;

    private String clientId = "Wz8BBhseiVn4cphvBlrS";
    // private String redirectUri = "http://54.252.108.194:80/auth/login/naver/callback";
    private String redirectUri = "http://localhost:8080/auth/login/naver/callback";

    private String state; // CSRF 방지용 임의 값

    @GetMapping("/login/naver")
    public ResponseEntity<Void> redirectToNaverLogin(HttpServletResponse response) throws IOException {

        // 네이버 로그인 인증 URL 생성
        String naverLoginUrl = String.format(
                "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&state=%s&prompt=login",
                clientId, redirectUri, state
        );

        // 네이버 로그인 페이지로 redirect
        response.sendRedirect(naverLoginUrl);
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
