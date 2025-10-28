package com.pleiades.interceptor;

import com.pleiades.model.TokenValidateResult;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.auth.AuthService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    public AuthInterceptor(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        log.info("AuthInterceptor preHandle");
        log.info("Request URI: {}", request.getRequestURI());

        if(request.getMethod().equals("OPTIONS")) { return true; }

        String authorization = request.getHeader("Authorization");
        if(authorization == null || authorization.isEmpty()) {
            response.setStatus(HttpStatus.PRECONDITION_REQUIRED.value());
            return false;
        }

        // admin user
        if(authorization.startsWith("admin")){
            log.info("auth - admin login");
            String email = authorization.split(" ")[1];
            request.setAttribute("email", email);
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        log.info("Authorization: {}", authorization);
        if (!authorization.startsWith("Bearer ")) {
            log.info("AuthInterceptor preHandle 401 - no authorization");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);       // 401
            return false;
        }

        String accessToken = HeaderUtil.authorizationBearer(authorization);

        TokenValidateResult tokenValidateResult = TokenValidateResult.of(accessToken);

        ValidationStatus tokenStatus = tokenValidateResult.getValidationStatus();

        if (tokenStatus.equals(ValidationStatus.NONE)) {
            log.info("AuthInterceptor preHandle 401 - no token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);       // 401
            return false;
        }

        if (tokenStatus.equals(ValidationStatus.NOT_VALID)) {
            log.info("AuthInterceptor preHandle 401");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);       // 401
            return false;
        }

        // 사용자 email 추출 후 request attribute 에 넣기
        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();

        // TODO
        if (email == null) {
            log.info("AuthInterceptor preHandle 401 - no email");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        request.setAttribute("email", email);

        log.info("AuthInterceptor preHandle 200");
        response.setStatus(HttpServletResponse.SC_OK);
        return true;
    }

}
