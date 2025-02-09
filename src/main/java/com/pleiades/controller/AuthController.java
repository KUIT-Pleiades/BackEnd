package com.pleiades.controller;

import com.pleiades.dto.ProfileDto;
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
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    // todo: user 정보가 존재하는지 검증 - 200, 202
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> login(@RequestHeader("Authorization") String authorization) {
        log.info("/auth");

        String accessToken = HeaderUtil.authorizationBearer(authorization);

        ValidationStatus userValidation = authService.userValidation(accessToken);

        // todo: message
        if (userValidation == ValidationStatus.NOT_VALID) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();    // user 존재: 200
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue("refreshToken") String refreshToken) {
        log.info("/auth/refresh");
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED) //428
                    .body(Map.of("message","Refresh Token is required"));
        }
        return authService.responseRefreshTokenStatus(refreshToken);
    }

    @GetMapping("/checkId")
    public ResponseEntity<Map<String, String>> checkId(HttpServletRequest request) {
        log.info("/auth/checkId");
        Map<String, String> body = new HashMap<>();
        String id = request.getParameter("userId");

        if (id == null || id.isEmpty()) {
            body.put("available", "false");
            body.put("message", "UserId is required.");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(body);
        }

        DuplicationService<User> duplicationService = new DuplicationService<>(userRepository);
        return duplicationService.responseIdDuplication(id);
    }

    // todo: AuthInterceptor 겹치는 부분 Refactor
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestHeader("Authorization") String authorization, @CookieValue("refreshToken") String refreshToken, @RequestBody UserInfoDto userInfoDto) {
        log.info("/auth/signup");

        try {
            // access token에서 email 추출
            String accessToken = HeaderUtil.authorizationBearer(authorization);

            Claims token = jwtUtil.validateToken(accessToken);
            String email = token.getSubject();   // email은 token의 subject에 저장되어 있음!

            ValidationStatus signupStatus = signupService.signup(email, userInfoDto, refreshToken);

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

    @PostMapping("/profile")
    public ResponseEntity<Map<String, String>> profile(@RequestHeader("Authorization") String authorization, @RequestBody ProfileDto profileDto) {
        log.info("/auth/profile");

        Optional<User> user = userRepository.findById(profileDto.getUserId());
        if (user.isPresent()) { // todo: profileUrl만 업데이트하는 메서드 추가
            user.get().setProfileUrl(profileDto.getProfileUrl());
            try {
                userRepository.save(user.get());
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}