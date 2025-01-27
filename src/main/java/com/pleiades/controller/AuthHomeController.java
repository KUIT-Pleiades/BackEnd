package com.pleiades.controller;

import com.pleiades.dto.ProfileDto;
import com.pleiades.dto.SignUpDto;
import com.pleiades.dto.character.response.ResponseCharacterFaceDto;
import com.pleiades.dto.character.response.ResponseCharacterItemDto;
import com.pleiades.dto.character.response.ResponseCharacterOutfitDto;
import com.pleiades.dto.character.response.ResponseStarBackgroundDto;
import com.pleiades.entity.*;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
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

    @Autowired
    AuthHomeController(UserRepository userRepository, StarRepository starRepository, KakaoTokenRepository kakaoTokenRepository,
                       NaverTokenRepository naverTokenRepository, ImageJsonCreator imageJsonCreator, SignupService signupService)
    {
        this.userRepository = userRepository; this.starRepository = starRepository;
        this.kakaoTokenRepository = kakaoTokenRepository; this.naverTokenRepository = naverTokenRepository;
        this.imageJsonCreator = imageJsonCreator; this.signupService = signupService;
    }
    // 첫 접속 화면
    // todo: user 존재하는지 확인 필요
    @PostMapping("")
    public ResponseEntity<Map<String, String>> login(HttpServletRequest request) throws IOException {
        String jwtAccessToken = request.getHeader("accessToken");
        Claims token = jwtUtil.validateToken(jwtAccessToken);

        // access token 유효한 경우
        if (token != null) {
            log.info("로그인: 앱 Access token 유효");
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        String jwtRefreshToken = request.getHeader("refreshToken");
        if(jwtRefreshToken == null) {
            // 프론트한테 refresh token 요청
            return ResponseEntity
                    .status(HttpStatus.PRECONDITION_REQUIRED) // 428
                    .body(Map.of("error", "Refresh Token is required"));
        }
//        else{
        Claims refreshToken = jwtUtil.validateToken(jwtRefreshToken);
        // 프론트한테 소셜 로그인 재요청
        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403
                    .body(Map.of("error", "Social login is required"));
        }

        // refresh token은 유효한 경우
        else{
            log.info("로그인: 앱 Refresh token만 유효 - " + jwtRefreshToken);
            String email = refreshToken.getSubject();
            // 새로 jwt 토큰들 생성 -> 프론트한테 넘겨줌
            jwtAccessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
            jwtRefreshToken = jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());

             return ResponseEntity
                     .status(HttpStatus.UNAUTHORIZED) // 401
                     .body(Map.of("accessToken", jwtAccessToken, "refreshToken", jwtRefreshToken));
        }
//        }
    }

    // todo: 이미 있는 사용자에 대한 처리
    @GetMapping("/signup")
    public ResponseEntity<Map<String, Object>> signupPage(HttpServletRequest request) {
        log.info("signup");
        Map<String, Object> body = new HashMap<>();

        // 캐릭터 이미지 전송
        ResponseCharacterFaceDto characterFaceDto = imageJsonCreator.makeCharacterFaceJson();
        ResponseCharacterItemDto characterItemDto = imageJsonCreator.makeCharacterItemJson();
        ResponseCharacterOutfitDto characterOutfitDto = imageJsonCreator.makeCharacterOutfitJson();
        ResponseStarBackgroundDto starBackgroundDto = imageJsonCreator.makeStarBackgroundJson();

        body.put("face", characterFaceDto);
        body.put("item", characterItemDto);
        body.put("outfit", characterOutfitDto);
        body.put("starBackground", starBackgroundDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
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
        ValidationStatus idValidation = duplicationService.checkIdDuplication(id);

        if (idValidation == ValidationStatus.NOT_VALID) {
            body.put("available", "false");
            body.put("message", "The username is already taken.");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(body);
        }

        body.put("available", "true");
        body.put("message", "The username is available.");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    // todo: id 중복 체크, 별 배경 선택 추가, 캐릭터 & 별 연결
    // todo: 앱 token 프론트와 통신 기능 -> 메소드 따로 추출
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignUpDto signUpDto, HttpServletRequest request) {
        String jwtAccessToken = request.getHeader("accessToken");
        ValidationStatus accessTokenStatus = AuthService.checkToken(jwtAccessToken);

        if (accessTokenStatus == ValidationStatus.NONE) {
            String jwtRefreshToken = request.getHeader("refreshToken");
            ResponseEntity<Map<String, String>> refreshTokenStatus = AuthService.responseRefreshTokenStatus(jwtRefreshToken);

            if (refreshTokenStatus != null) { return refreshTokenStatus; }

            // refresh token은 유효한 경우
            log.info("회원가입: Refresh token 유효");
            Claims refreshToken = jwtUtil.validateToken(jwtRefreshToken);

            String email = refreshToken.getSubject();

            // 새로 jwt 토큰들 생성 -> 프론트한테 넘겨줌
            jwtAccessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
            jwtRefreshToken = jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("accessToken", jwtAccessToken, "refreshToken", jwtRefreshToken));
        }

        if (accessTokenStatus == ValidationStatus.NOT_VALID) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Access Token expired - Refresh Token is required."));
        }

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