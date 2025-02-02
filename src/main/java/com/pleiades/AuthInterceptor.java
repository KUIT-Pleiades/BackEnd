package com.pleiades;

import com.pleiades.entity.User;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.AuthService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthInterceptor(JwtUtil jwtUtil, UserRepository userRepository, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String authorization = request.getHeader("Authorization");
        String accessToken = HeaderUtil.authorizationBearer(authorization);

        ValidationStatus tokenStatus = authService.checkToken(accessToken);

        if (tokenStatus.equals(ValidationStatus.NONE)) {
            response.sendError(428, "Precondition Required");      // 428
            return false;
        }

        if (tokenStatus.equals(ValidationStatus.NOT_VALID)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);       // 401
            return false;
        }

        Claims claims = jwtUtil.validateToken(accessToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            response.sendError(HttpServletResponse.SC_ACCEPTED);        // user 없음: 202
            return false;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return true;
    }

}
