package com.pleiades.service;

import com.pleiades.dto.NaverLoginResponse;
import com.pleiades.entity.NaverToken;
import com.pleiades.entity.User;
import com.pleiades.exception.NaverRefreshTokenExpiredException;
import com.pleiades.repository.NaverTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.util.NaverApiUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverLoginService {

    private final NaverApiUtil naverApiUtil;
    private final NaverTokenRepository naverTokenRepository;
    private final UserRepository userRepository;
    private final HttpSession session;

    @Transactional
    public ResponseEntity<?> handleNaverLoginCallback(String code, String state) {
        log.info("service 계층 진입");

        Map<String, String> naverTokens = naverApiUtil.getTokens(code, state);
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

        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            NaverToken naverToken = naverTokenRepository.findByUserEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("에러 : user의 email로 naverToken 못찾음"));

            accessToken = naverApiUtil.getValidAccessToken(naverToken);

            naverToken.setAccessToken(accessToken);
            naverToken.setLastUpdated(System.currentTimeMillis());
            naverTokenRepository.save(naverToken);

            updateAppTokensForUser(user);

            return ResponseEntity.ok(userInfo);

        } else {
            session.setAttribute("naverRefreshToken", refreshToken);
            session.setAttribute("naverAccessToken", accessToken);
            session.setAttribute("email", email);

            return ResponseEntity.status(302)
                    .header("Location", "http://54.252.108.194/auth/signup")
                    .build();
        }
    }

    @Transactional
    public NaverLoginResponse handleRefreshTokenLogin(String refreshToken) {

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

    // session 저장으로 변경
//    private void saveNewUserAndTokens(String email, String accessToken, String refreshToken) {
//        User user = new User();
//        user.setEmail(email);
//        user.setRefreshToken(refreshToken);
//        user.setCreatedDate(LocalDate.now());
//
//        NaverToken naverToken = new NaverToken();
//        naverToken.setAccessToken(accessToken);
//        naverToken.setRefreshToken(refreshToken);
//        naverToken.setLastUpdated(System.currentTimeMillis());
//
//        naverToken.setUser(user);
//        user.setNaverToken(naverToken);
//
//        userRepository.save(user);
//    }

    private void updateAppTokensForUser(User user) {
        log.info("앱 자체 토큰 갱신 완료 for user: {}", user.getEmail());
    }
}