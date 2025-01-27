package com.pleiades.service;

import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class AuthService {
    @Autowired
    static JwtUtil jwtUtil;

    public static ValidationStatus checkToken(String token) {
        if (token == null || token.isEmpty()) { return ValidationStatus.NONE; }

        Claims tokenClaim = jwtUtil.validateToken(token);
        if (tokenClaim == null) { return ValidationStatus.NOT_VALID; }

        return ValidationStatus.VALID;
    }

    public static ResponseEntity<Map<String, String>> responseRefreshTokenStatus(String token) {
        Map<String, String> body = new HashMap<>();
        ValidationStatus tokenStatus = checkToken(token);
        if (tokenStatus == ValidationStatus.NONE ) {
            body.put("message", "refresh token required");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(body);
        }
        if (tokenStatus == ValidationStatus.NOT_VALID) {
            body.put("message", "refresh token expired - social login required");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(body);
        }
        return null;
    }
}
