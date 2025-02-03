package com.pleiades.interceptor;

import com.pleiades.repository.UserRepository;
import com.pleiades.service.AuthService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    public AuthInterceptor(JwtUtil jwtUtil, UserRepository userRepository, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        log.info("AuthInterceptor preHandle");

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("AuthInterceptor preHandle 428");
            response.sendError(428, "Precondition Required");      // 428
            return false;
        }

        String accessToken = HeaderUtil.authorizationBearer(authorization);

        ValidationStatus tokenStatus = authService.checkToken(accessToken);

        if (tokenStatus.equals(ValidationStatus.NONE)) {
            log.info("AuthInterceptor preHandle 428");
            response.sendError(428, "Precondition Required");      // 428
            return false;
        }

        if (tokenStatus.equals(ValidationStatus.NOT_VALID)) {
            log.info("AuthInterceptor preHandle 401");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);       // 401
            return false;
        }

        log.info("AuthInterceptor preHandle 200");
        response.setStatus(HttpServletResponse.SC_OK);
        return true;
    }

}
