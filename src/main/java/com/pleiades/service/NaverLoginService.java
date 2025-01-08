package com.pleiades.service;

import com.pleiades.dto.NaverLoginResponse;
import com.pleiades.entity.User;
import com.pleiades.repository.UserRepository;
import com.pleiades.util.NaverApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// todo : User entity Field 에 맞춰서 재구성
@Slf4j
@Service
@RequiredArgsConstructor
public class NaverLoginService {
    private final UserRepository userRepository;
    private final NaverApiUtil naverApiUtil;

    public NaverLoginResponse handleNaverLoginCallback(String code, String state) {

        String accessToken = naverApiUtil.getAccessToken(code, state);
        NaverLoginResponse userInfo = naverApiUtil.getUserInfo(accessToken);

        User user = userRepository.findByNaverId(userInfo.getNaverId())
                .orElse(User.builder()
                        .naverId(userInfo.getNaverId())
                        .userName(userInfo.getUserName())
                        .birthDate(userInfo.getBirthDate())
                        .build());
        log.info("로그인 서비스 userName: {}", user.getUserName());
        log.info("로그인 서비스 ID: {}", user.getNaverId());

        user.setAccessToken(accessToken);
        userRepository.save(user);

        return userInfo;
    }
}
