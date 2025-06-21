package com.pleiades.util;

import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
//    @Value("${JWT_SECRET_KEY}")
    private String secretKey = System.getenv("JWT_SECRET_KEY");
    private final long ACCESS_TOKEN_EXPIRATION_MS = 3600000; // 1 hour
    private final long REFRESH_TOKEN_EXPIRATION_MS = 604800000L;    // 1 week

    private String generateToken(String subject, String role, long expirationMs) {
        return Jwts.builder()
                .setSubject(subject)   // subject = 사용자 이메일
                .claim("role", role)  // role 클레임 추가
                .setIssuedAt(new Date())    // 발행 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // 만료 시간 설정 (1시간 후)
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 서명 설정
                .compact(); // JWT 문자열로 반환
    }

    public String generateAccessToken(String subject, String role) {
        return generateToken(subject, role, ACCESS_TOKEN_EXPIRATION_MS);
    }

    public String generateRefreshToken(String subject, String role) {
        return generateToken(subject, role, REFRESH_TOKEN_EXPIRATION_MS);
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            CustomException customException = new CustomException(ErrorCode.INVALID_TOKEN);
            log.error(customException.getMessage(), customException);
            return null;
        }
    }
}