package com.pleiades.service;

import com.pleiades.dto.KakaoTokenDto;
import com.pleiades.dto.KakaoUserDto;
import com.pleiades.repository.KakaoTokenRepository;
import com.pleiades.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KakaoTokenService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    KakaoTokenRepository kakaoTokenRepository;

    public boolean checkAccessTokenValidation(String accessToken, String userId) {
        String foundUserId = null;
        KakaoUserDto kakaoUserDto = KakaoRequest.postUserEmail(accessToken);
        if (kakaoUserDto != null) { foundUserId = userRepository.findByEmail(kakaoUserDto.getKakaoAccount().getEmail()).get().getId(); }
        return (userId == foundUserId);
    }

    public String checkRefreshTokenValidation(String userId) {
        String refreshToken = kakaoTokenRepository.findByUser_Id(userId).get().getRefreshToken();
        KakaoTokenDto refreshedAccessToken = KakaoRequest.postRefreshAccessToken(refreshToken);

        if ( refreshedAccessToken != null ) return refreshedAccessToken.getAccessToken();
        return null;
    }

}
