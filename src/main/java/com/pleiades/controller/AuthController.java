package com.pleiades.controller;

import com.pleiades.dto.UserInfoDto;
import com.pleiades.entity.*;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.model.TokenValidateResult;
import com.pleiades.repository.*;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.DuplicationService;
import com.pleiades.service.auth.SignupService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.pleiades.exception.ErrorCode.INVALID_TOKEN;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final StarRepository starRepository;

    private final KakaoTokenRepository kakaoTokenRepository;
    private final NaverTokenRepository naverTokenRepository;

    private final SignupService signupService;
    private final AuthService authService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> login(@RequestHeader("Authorization") String authorization) {
        log.info("/auth");
        return ResponseEntity.status(HttpStatus.OK).build();  // user 존재 여부는 /home 에서
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        log.info("/auth/refresh");
        log.info("cookie - refreshToken: " + refreshToken);

        return authService.responseRefreshTokenStatus(refreshToken);
    }

    @GetMapping("/checkId")
    public ResponseEntity<Map<String, Object>> checkId(HttpServletRequest request) {
        log.info("/auth/checkId");
        Map<String, Object> body = new HashMap<>();
        String id = request.getParameter("userId");

        if (id == null || id.isEmpty()) {
            body.put("available", false);
            body.put("message", "UserId is required.");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(body);
        }

        DuplicationService<User> duplicationService = new DuplicationService<>(userRepository);
        return duplicationService.responseIdDuplication(id);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestHeader("Authorization") String authorization, @Valid @RequestBody UserInfoDto userInfoDto) {
        log.info("/auth/signup");

        try {
            String email = authService.getEmailByAuthorization(authorization);

            ValidationStatus signupStatus = signupService.signup(email, userInfoDto);

            if (signupStatus == ValidationStatus.NOT_VALID) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","you need to login by social"));      // 401
            }
            if (signupStatus == ValidationStatus.DUPLICATE) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(Map.of("message","duplicate user"));     // 208
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "sign-up success - character created")); // 201 : 회원가입 완료
        } catch (Exception e) {
            log.info("sign-up failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("message","failed to save sign-up information"));   // 422
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, @CookieValue("refreshToken") String refreshToken) {
        log.info("/auth/logout");
        String email = TokenValidateResult.of(refreshToken).getEmail();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","unvalid refresh token"));
        }
        authService.logout(email);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(HttpServletRequest request, @CookieValue("refreshToken") String refreshToken) {
        log.info("/auth/withdraw");
        String email = (String) request.getAttribute("email");
        if(email == null) { throw new CustomException(ErrorCode.INVALID_TOKEN);}
        authService.withdraw(email);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}