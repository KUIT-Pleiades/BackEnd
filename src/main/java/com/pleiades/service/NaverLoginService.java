package com.pleiades.service;

import com.pleiades.dto.LoginCBResponseDto;
import com.pleiades.dto.naver.NaverLoginResponse;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverLoginService {

    private final NaverApiUtil naverApiUtil;
    private final NaverTokenRepository naverTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginCBResponseDto handleNaverLoginCallback(String code) {
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

        NaverLoginResponse userInfo = naverApiUtil.getUserInfo(accessToken);
        String email = userInfo.getEmail();

        // todo : naverTokenRepository.findByEmail
        User user = userRepository.findByEmail(email).orElse(null);
        LoginCBResponseDto cbResponse;

        if (user != null) {
            log.info("user 이미 존재");
            NaverToken naverToken = naverTokenRepository.findByUserEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("에러 : user의 email로 naverToken 못찾음"));

            accessToken = naverApiUtil.getValidAccessToken(naverToken);

            naverToken.setAccessToken(accessToken);
            naverToken.setLastUpdated(System.currentTimeMillis());
            naverTokenRepository.save(naverToken);

            cbResponse = updateAppTokensForUser(user);
        } else {
            user = new User();
            log.info("user 새로 생성");

            NaverToken naverToken = new NaverToken();
            naverToken.setAccessToken(accessToken);
            naverToken.setEmail(email);
            naverToken.setRefreshToken(refreshToken);
            naverToken.setLastUpdated(System.currentTimeMillis());
            naverTokenRepository.save(naverToken);

            user.setEmail(email);
            cbResponse = updateAppTokensForUser(user);
        }
        return cbResponse;
    }

    private LoginCBResponseDto updateAppTokensForUser(User user) {

        String jwtAccessToken = jwtUtil.generateAccessToken(user.getEmail(), JwtRole.ROLE_USER.getRole());
        String jwtRefreshToken = jwtUtil.generateRefreshToken(user.getEmail(), JwtRole.ROLE_USER.getRole());

        user.setRefreshToken(jwtRefreshToken);
        user.setAccessToken(jwtAccessToken);

        log.info("앱 자체 토큰 갱신 완료 for user: {}", user.getEmail());
        return new LoginCBResponseDto(jwtRefreshToken, jwtAccessToken);
    }

    // todo : 일단 안씀 -> 나중에 검사 해보고 지울 것
    @Transactional
    public NaverLoginResponse handleNaverRefreshTokenLogin(String refreshToken) {

        NaverToken naverToken = naverTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("DB에 네이버 Refresh Token 존재 X"));

        String accessToken = naverApiUtil.getValidAccessToken(naverToken);

        if (accessToken == null) {
            throw new NaverRefreshTokenExpiredException("네이버 access token 존재 X");
        }

        naverToken.setAccessToken(accessToken);
        naverToken.setLastUpdated(System.currentTimeMillis());
        naverTokenRepository.save(naverToken);

        NaverLoginResponse userInfo = naverApiUtil.getUserInfo(accessToken);
        log.info("Refresh token으로 사용자 정보 조회 성공: {}", userInfo);

        return userInfo;
    }
}