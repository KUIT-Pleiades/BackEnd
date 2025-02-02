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

import java.io.IOException;
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

        ValidationStatus tokenStatus = authService.checkToken(accessToken);

        if (tokenStatus.equals(ValidationStatus.NONE)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();      // 428
        }

        if (tokenStatus.equals(ValidationStatus.NOT_VALID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();      // 401
        }

        Claims claims = jwtUtil.validateToken(accessToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.ACCEPTED).build(); }   // user 없음: 202

        return authService.responseUserInfo(accessToken);    // user 존재: 200
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestHeader("Authorization") String authorization) {
        Map<String, String> body = new HashMap<>();
        String refreshToken = HeaderUtil.authorizationBearer(authorization);

        ValidationStatus tokenStatus = authService.checkToken(refreshToken);

        if (tokenStatus.equals(ValidationStatus.NONE)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();     // 428
        }

        if (tokenStatus.equals(ValidationStatus.NOT_VALID)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();     // 403
        }

        Claims claims = jwtUtil.validateToken(refreshToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.ACCEPTED).build(); }   // 202

        if (!user.get().getRefreshToken().equals(refreshToken)) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }   // 403

        String newAccessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());

        Cookie cookie = authService.setRefreshToken(newRefreshToken);
        body.put("accessToken", newAccessToken);

        return ResponseEntity
                .status(HttpStatus.CREATED)     // 201
                .header("Set-Cookie", cookie.toString())
                .body(body);
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
        String accessToken = HeaderUtil.authorizationBearer(authorization);

        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();   // email은 token의 subject에 저장되어 있음!

        signupService.signup(email, signUpDto);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 : 회원가입 완료
    }

    @PostMapping("/profile")
    public ResponseEntity<Map<String, String>> profile(@RequestBody ProfileDto profileDto) {
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