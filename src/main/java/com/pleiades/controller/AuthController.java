package com.pleiades.controller;

import com.pleiades.dto.UserInfoDto;
import com.pleiades.entity.*;
import com.pleiades.repository.*;
import com.pleiades.service.AuthService;
import com.pleiades.service.DuplicationService;
import com.pleiades.service.ImageJsonCreator;
import com.pleiades.service.SignupService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {
    JwtUtil jwtUtil;

    UserRepository userRepository;
    StarRepository starRepository;

    KakaoTokenRepository kakaoTokenRepository;
    NaverTokenRepository naverTokenRepository;

    ImageJsonCreator imageJsonCreator;

    SignupService signupService;
    AuthService authService;

    @Autowired
    AuthController(UserRepository userRepository, StarRepository starRepository, KakaoTokenRepository kakaoTokenRepository,
                   NaverTokenRepository naverTokenRepository,
                   ImageJsonCreator imageJsonCreator, SignupService signupService, AuthService authService,
                   JwtUtil jwtUtil, HeaderUtil headerUtil)
    {
        this.userRepository = userRepository; this.starRepository = starRepository;
        this.kakaoTokenRepository = kakaoTokenRepository; this.naverTokenRepository = naverTokenRepository;
        this.imageJsonCreator = imageJsonCreator; this.signupService = signupService; this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> login(@RequestHeader("Authorization") String authorization) {
        log.info("/auth");
        return ResponseEntity.status(HttpStatus.OK).build();  // user 존재 여부는 /home 에서
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        log.info("/auth/refresh");
        log.info("cookie - refreshToken: " + refreshToken);
        if (refreshToken == null) {
            log.info("cookie - refreshToken is null");
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED) //428
                    .body(Map.of("message","Refresh Token is required"));
        }
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
    public ResponseEntity<Map<String, String>> signup(@RequestHeader("Authorization") String authorization, @RequestBody UserInfoDto userInfoDto) {
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
}