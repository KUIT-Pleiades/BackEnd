package com.pleiades.model;

import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TokenValidateResult {
    private final String token;
    private final ValidationStatus validationStatus;
    private final Claims claims;

    private TokenValidateResult(String token, ValidationStatus validationStatus, Claims claims) {
        this.token = token;
        this.validationStatus = validationStatus;
        this.claims = claims;
    }

    public static TokenValidateResult of(String token, JwtUtil jwtUtil) {
        Claims claims = null;
        if (token == null || token.isEmpty()) {
            return new TokenValidateResult(token, ValidationStatus.NONE, claims);
        }

        claims = jwtUtil.validateToken(token);

        if (claims == null) {
            return new TokenValidateResult(token, ValidationStatus.NOT_VALID, claims);
        }

        return new TokenValidateResult(token, ValidationStatus.VALID, claims);
    }

    public String getEmail() {
        if (validationStatus == ValidationStatus.VALID) {
            return claims.getSubject();
        }
        return null;
    }

}
