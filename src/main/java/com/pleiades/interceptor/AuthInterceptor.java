package com.pleiades.interceptor;

import com.pleiades.entity.User;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.AuthService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    public AuthInterceptor(JwtUtil jwtUtil, UserRepository userRepository, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        log.info("AuthInterceptor preHandle");
        log.info("Request URI: {}", request.getRequestURI());

        if(request.getMethod().equals("OPTIONS")) { return true; }

        String authorization = request.getHeader("Authorization");
        if(authorization.startsWith("admin")){
            log.info("auth - admin login");
            return true;   // admin user
        }

        log.info("Authorization: {}", authorization);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("AuthInterceptor preHandle 428 - no authorization");
            response.sendError(428, "Precondition Required");      // 428
            return false;
        }

        String accessToken = HeaderUtil.authorizationBearer(authorization);

        ValidationStatus tokenStatus = authService.checkToken(accessToken);

        if (tokenStatus.equals(ValidationStatus.NONE)) {
            log.info("AuthInterceptor preHandle 428 - no token");
            response.sendError(428, "Precondition Required");      // 428
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
        request.setAttribute("email", email);

        log.info("AuthInterceptor preHandle 200");
        response.setStatus(HttpServletResponse.SC_OK);
        return true;
    }

}
