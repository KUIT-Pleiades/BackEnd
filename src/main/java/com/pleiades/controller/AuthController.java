package com.pleiades.controller;

import com.pleiades.dto.UserInfoDto;
import com.pleiades.entity.*;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.exception.CustomException;
import com.pleiades.model.TokenValidateResult;
import com.pleiades.repository.*;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.DuplicationService;
import com.pleiades.service.auth.SignupService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    private final SignupService signupService;
    private final AuthService authService;

    @Operation(summary = "로그인", description = "로그인")
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> login(@RequestHeader("Authorization") String authorization) {
        log.info("/auth");
        return ResponseEntity.status(HttpStatus.OK).build();  // user 존재 여부는 /home 에서
    }

    @Operation(summary = "자동 로그인", description = "리프레시 토큰 확인")
    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        log.info("/auth/refresh");
        log.info("cookie - refreshToken: " + refreshToken);

        return authService.responseRefreshTokenStatus(refreshToken);
    }

    @Operation(summary = "중복 체크", description = "아이디 중복 체크")
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

    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(HttpServletRequest request, @Valid @RequestBody UserInfoDto userInfoDto) {
        log.info("/auth/signup");
        String email = request.getAttribute("email").toString();

        try {
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

    @Operation(summary = "로그아웃", description = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, @CookieValue("refreshToken") String refreshToken) {
        log.info("/auth/logout");
        String email = request.getAttribute("email").toString();

        authService.logout(email);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(HttpServletRequest request, @CookieValue("refreshToken") String refreshToken) {
        log.info("/auth/withdraw");
        String email = request.getAttribute("email").toString();
        authService.withdraw(email);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}