package com.pleiades.service;

import com.pleiades.strings.JwtRole;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AuthService {
    @Autowired
    JwtUtil jwtUtil;

//    public ResponseEntity<Map<String, String>> checkAccessToken(String token) {
//        Map<String, String> body = new HashMap<>();
//
//        String accessToken = request.getHeader("AccessToken");
//        String refreshToken = request.getHeader("RefreshToken");
//        if (accessToken == null) { return checkRefreshToken(refreshToken, body); }
//
//        Claims token = jwtUtil.validateToken(accessToken);
//        if (token == null) {return checkRefreshToken(refreshToken, body); }
//        String userId = token.getId();
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .header("Location", "/star?userId="+userId)
//                .body(body);
//    }

    private ResponseEntity<Map<String, String>> checkRefreshToken(String refreshToken, Map<String, String> body) {
        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", "/auth/login")
                    .build();
        }

        Claims token = jwtUtil.validateToken(refreshToken);
        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", "/auth/login")
                    .build();
        }
        String userId = token.getId();
        String accessToken = jwtUtil.generateAccessToken(userId, JwtRole.ROLE_USER.getRole());

        body.put("AccessToken", accessToken);
        log.info("(c) Access token: " + accessToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Location", "/star?userId="+userId)
                .body(body);
    }
}
