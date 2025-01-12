package com.pleiades.service;

import com.pleiades.dto.NaverLoginResponse;
import com.pleiades.entity.NaverToken;
import com.pleiades.entity.User;
import com.pleiades.repository.NaverTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.util.NaverApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public NaverLoginResponse handleNaverLoginCallback(String code, String state) {

        Map<String, String> naverTokens = naverApiUtil.getTokens(code, state);
        if (naverTokens == null) {
            throw new IllegalArgumentException("에러: Naver API로부터 token들 받아오기 실패");
        }
        log.info("Naver API Response: {}", naverTokens);
        if (naverTokens.get("access_token") == null) {
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
        } else {
            // todo : 사용자가 ID, name을 입력하는 logic
            log.info("새로운 사용자 : ID, name 입력 필요");
            saveNewUserAndTokens(email, accessToken, refreshToken);
        }

        return userInfo;
    }

    private void saveNewUserAndTokens(String email, String accessToken, String refreshToken) {
        User user = new User();
        user.setEmail(email);
        user.setRefreshToken(refreshToken);
        user.setCreatedDate(LocalDate.now());

        NaverToken naverToken = new NaverToken();
        naverToken.setAccessToken(accessToken);
        naverToken.setRefreshToken(refreshToken);
        naverToken.setLastUpdated(System.currentTimeMillis());

        naverToken.setUser(user);
        user.setNaverToken(naverToken);

        userRepository.save(user);
    }

    // 앱 자체의 refresh token 및 access token
    private void updateAppTokensForUser(User user) {
        log.info("앱 자체 토큰 갱신 완료 for user: {}", user.getEmail());
    }
}

