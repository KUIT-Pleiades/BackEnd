package com.pleiades.service;

import com.pleiades.dto.LoginResponseDto;
import com.pleiades.dto.naver.NaverLoginResponseDto;
import com.pleiades.entity.NaverToken;
import com.pleiades.entity.User;
import com.pleiades.exception.NaverRefreshTokenExpiredException;
import com.pleiades.repository.NaverTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.JwtRole;
import com.pleiades.util.JwtUtil;
import com.pleiades.util.NaverApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverLoginService {

    private final NaverApiUtil naverApiUtil;
    private final NaverTokenRepository naverTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponseDto handleNaverLoginCallback(String code) {
        log.info("service 계층 진입");

        Map<String, String> naverTokens = naverApiUtil.getTokens(code);
        if (naverTokens == null) {
            log.error("에러: Naver API로부터 token들 받아오기 실패");
            throw new IllegalArgumentException("에러: Naver API로부터 token들 받아오기 실패");
        }
        log.info("Naver API Response: {}", naverTokens);
        if (naverTokens.get("access_token") == null) {
            log.error("에러: Naver API로부터 access token 받아오기 실패");
            throw new IllegalArgumentException("에러: Naver API로부터 access token 받아오기 실패");
        }

        String accessToken = naverTokens.get("access_token");
        String refreshToken = naverTokens.get("refresh_token");

        NaverLoginResponseDto userInfo = naverApiUtil.getUserInfo(accessToken);
        String email = userInfo.getEmail();

        User user = userRepository.findByEmail(email).orElse(null);
        NaverToken naverToken = naverTokenRepository.findByEmail(email).orElse(null);

        if (user != null) {
            log.info("기존 유저 네이버 로그인: userId = {}", user.getEmail());
            return processExistingUser(user, naverToken, accessToken, refreshToken);
        }

        if (naverToken != null) {
            log.info("네이버 토큰만 존재하는 유저: email = {}", email);
            return processUserWithoutSignup(naverToken, accessToken, refreshToken);
        }

        log.info("네이버 로그인 정보만 있는 신규 유저 저장: email = {}", email);
        return processNewNaverUser(email, accessToken, refreshToken);
    }

    private LoginResponseDto processExistingUser(User user, NaverToken naverToken, String accessToken, String refreshToken) {
        if (naverToken == null) {
            log.error("기존 user NaverToken 존재 X");
            throw new IllegalStateException("기존 user NaverToken 존재 X");
        }

        // accessToken = naverApiUtil.getValidAccessToken(naverToken);
        // naverToken.setAccessToken(accessToken);
        naverToken.setLastUpdated(System.currentTimeMillis());
        naverTokenRepository.save(naverToken);

        return generateAppTokens(user);
    }

    private LoginResponseDto processUserWithoutSignup(NaverToken naverToken, String accessToken, String refreshToken) {
        naverToken.setRefreshToken(refreshToken);
        naverToken.setLastUpdated(System.currentTimeMillis());
        naverTokenRepository.save(naverToken);

        return generateAppTokens(naverToken.getEmail());
    }

    private LoginResponseDto processNewNaverUser(String email, String accessToken, String refreshToken) {
        NaverToken naverToken = new NaverToken();
        naverToken.setEmail(email);
        naverToken.setRefreshToken(refreshToken);
        naverToken.setLastUpdated(System.currentTimeMillis());
        naverTokenRepository.save(naverToken);

        return generateAppTokens(email);
    }

    private LoginResponseDto generateAppTokens(String email) {
        String jwtAccessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
        String jwtRefreshToken = jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());
        log.info("앱 자체 토큰 생성 완료 for email: {}", email);
        return new LoginResponseDto(jwtRefreshToken, jwtAccessToken);
    }

    private LoginResponseDto generateAppTokens(User user) {
        String jwtAccessToken = jwtUtil.generateAccessToken(user.getEmail(), JwtRole.ROLE_USER.getRole());
        String jwtRefreshToken = jwtUtil.generateRefreshToken(user.getEmail(), JwtRole.ROLE_USER.getRole());

        user.setRefreshToken(jwtRefreshToken);
        userRepository.save(user);
        log.info("앱 자체 토큰 생성 완료 for user: {}", user.getEmail());

        return new LoginResponseDto(jwtRefreshToken, jwtAccessToken);
    }

    // todo : 일단 안씀 -> 나중에 검사 해보고 지울 것
    @Transactional
    public NaverLoginResponseDto handleNaverRefreshTokenLogin(String refreshToken) {

        NaverToken naverToken = naverTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("DB에 네이버 Refresh Token 존재 X"));

        String accessToken = naverApiUtil.getValidAccessToken(naverToken);

        if (accessToken == null) {
            throw new NaverRefreshTokenExpiredException("네이버 access token 존재 X");
        }

        naverToken.setAccessToken(accessToken);
        naverToken.setLastUpdated(System.currentTimeMillis());
        naverTokenRepository.save(naverToken);

        NaverLoginResponseDto userInfo = naverApiUtil.getUserInfo(accessToken);
        log.info("Refresh token으로 사용자 정보 조회 성공: {}", userInfo);

        return userInfo;
    }
}