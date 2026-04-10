package com.pleiades.service;

import com.pleiades.entity.FcmToken;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FcmTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.DeviceType;
import com.pleiades.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void upsert(String email, String token, DeviceType deviceType) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        fcmTokenRepository.findByToken(token)
                .ifPresentOrElse(
                        FcmToken::updateTimestamp,
                        () -> fcmTokenRepository.save(FcmToken.builder()
                                .user(user)
                                .token(token)
                                .deviceType(deviceType)
                                .createdAt(LocalDateTimeUtil.now())
                                .updatedAt(LocalDateTimeUtil.now())
                                .build())
                );
    }

    @Transactional
    public void delete(String token) {
        fcmTokenRepository.deleteByToken(token);
    }
}
