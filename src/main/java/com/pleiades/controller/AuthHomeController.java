package com.pleiades.controller;

import com.pleiades.dto.ProfileDto;
import com.pleiades.dto.SignUpDto;
import com.pleiades.dto.character.response.ResponseCharacterFaceDto;
import com.pleiades.dto.character.response.ResponseCharacterItemDto;
import com.pleiades.dto.character.response.ResponseCharacterOutfitDto;
import com.pleiades.dto.character.response.ResponseStarBackgroundDto;
import com.pleiades.entity.*;
import com.pleiades.repository.*;
import com.pleiades.service.AuthService;
import com.pleiades.service.DuplicationService;
import com.pleiades.service.ImageJsonCreator;
import com.pleiades.service.SignupService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.JwtUtil;
import com.pleiades.strings.JwtRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthHomeController {
    private final JwtUtil jwtUtil = new JwtUtil();

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
                       ImageJsonCreator imageJsonCreator, SignupService signupService, AuthService authService)
    {
        this.userRepository = userRepository; this.starRepository = starRepository;
        this.kakaoTokenRepository = kakaoTokenRepository; this.naverTokenRepository = naverTokenRepository;
        this.imageJsonCreator = imageJsonCreator; this.signupService = signupService; this.authService = authService;
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> login(HttpServletRequest request) {
        String jwtAccessToken = request.getHeader("accessToken");
        String jwtRefreshToken = request.getHeader("refreshToken");

        // access, refresh 검사
        ResponseEntity<Map<String, String>> response = authService.responseTokenValidation(jwtAccessToken, jwtRefreshToken);
        if (response != null) return response;  // null이면 access 유효

        Claims claims = jwtUtil.validateToken(jwtAccessToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) { return ResponseEntity.status(HttpStatus.ACCEPTED).build(); }   // user 없음: 202

        return authService.responseUserInfo(jwtAccessToken);    // user 존재: 200
    }

    // 이미 있는 사용자에 대한 처리 - 내가 이걸 왜 써놨을까 언니..
    @GetMapping("/signup")
    public ResponseEntity<Map<String, Object>> signupPage(HttpServletRequest request) {
        log.info("signup");
        return imageJsonCreator.makeAllJson();
    }

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
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignUpDto signUpDto, HttpServletRequest request) {
        String jwtAccessToken = request.getHeader("accessToken");
        String jwtRefreshToken = request.getHeader("refreshToken");

        ResponseEntity<Map<String, String>> response = authService.responseTokenValidation(jwtAccessToken, jwtRefreshToken);
        if (response != null) { return response; }

        // access token 유효한 경우 -> naver / kakao 랑 user 매핑
        log.info("회원가입: Access token 유효");

        Claims token = jwtUtil.validateToken(jwtAccessToken);
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