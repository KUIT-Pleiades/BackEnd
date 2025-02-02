package com.pleiades.controller;

import com.pleiades.dto.ProfileDto;
import com.pleiades.dto.SignUpDto;
import com.pleiades.entity.*;
import com.pleiades.repository.*;
import com.pleiades.service.AuthService;
import com.pleiades.service.DuplicationService;
import com.pleiades.service.ImageJsonCreator;
import com.pleiades.service.SignupService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import com.pleiades.strings.JwtRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
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
public class AuthHomeController {
    JwtUtil jwtUtil;

    UserRepository userRepository;
    StarRepository starRepository;

    KakaoTokenRepository kakaoTokenRepository;
    NaverTokenRepository naverTokenRepository;

    ImageJsonCreator imageJsonCreator;

    SignupService signupService;
    AuthService authService;

    @Autowired
    AuthHomeController(UserRepository userRepository, StarRepository starRepository, KakaoTokenRepository kakaoTokenRepository,
                       NaverTokenRepository naverTokenRepository,
                       ImageJsonCreator imageJsonCreator, SignupService signupService, AuthService authService,
                       JwtUtil jwtUtil, HeaderUtil headerUtil)
    {
        this.userRepository = userRepository; this.starRepository = starRepository;
        this.kakaoTokenRepository = kakaoTokenRepository; this.naverTokenRepository = naverTokenRepository;
        this.imageJsonCreator = imageJsonCreator; this.signupService = signupService; this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> login(@RequestHeader("Authorization") String authorization) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);

        ResponseEntity<Map<String, String>> response = authService.responseAccessTokenStatus(accessToken);
        if (response.getStatusCode() != HttpStatus.OK) { return response; }

        return authService.responseUserInfo(accessToken);    // user 존재: 200
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        return authService.responseRefreshTokenStatus(refreshToken);
    }

/*  필요 없는 거 맞져
    @GetMapping("/signup")
    public ResponseEntity<Map<String, Object>> signupPage(HttpServletRequest request) {
        log.info("signup");
        return imageJsonCreator.makeAllJson();
    }*/

    @GetMapping("/checkId")
    public ResponseEntity<Map<String, String>> checkId(HttpServletRequest request) {
        Map<String, String> body = new HashMap<>();
        String id = request.getParameter("id");

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

    // todo: id 중복 체크, 별 배경 선택 추가, 캐릭터 & 별 연결
    // todo: 앱 token 프론트와 통신 기능 -> 메소드 따로 추출
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestHeader("Authorization") String authorization, @RequestBody SignUpDto signUpDto) {
        // accessToken 검사
        String accessToken = HeaderUtil.authorizationBearer(authorization);
        ResponseEntity<Map<String, String>> response = authService.responseAccessTokenStatus(accessToken);
        if (response.getStatusCode() != HttpStatus.OK) { return response; }

        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();   // email은 token의 subject에 저장되어 있음!

        signupService.signup(email, signUpDto);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 : 회원가입 완료
    }

    @PostMapping("/profile")
    public ResponseEntity<Map<String, String>> profile(@RequestHeader("Authorization") String authorization, @RequestBody ProfileDto profileDto) {
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