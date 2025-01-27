package com.pleiades.service;

import com.pleiades.strings.TokenStatus;
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

    public static TokenStatus checkToken(String token) {
        if (token == null) { return TokenStatus.NONE; }

        Claims tokenClaim = jwtUtil.validateToken(token);
        if (tokenClaim == null) { return TokenStatus.NOT_VALID; }

        return TokenStatus.VALID;
    }

    public static ResponseEntity<Map<String, String>> responseTokenStatus(String token) {
        Map<String, String> body = new HashMap<>();
        TokenStatus tokenStatus = checkToken(token);
        if (tokenStatus == TokenStatus.NONE ) {
            body.put("error", "no token found");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(body);
        }
        if (tokenStatus == TokenStatus.NOT_VALID) {
            body.put("error", "invalid token");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(body);
        }
        return null;
    }
}
