package com.pleiades.service;

import com.pleiades.dto.NotificationSettingDto;
import com.pleiades.entity.NotificationSetting;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.NotificationSettingRepository;
import com.pleiades.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationSettingDto getSetting(String email) {
        User user = getUser(email);
        NotificationSetting setting = getOrCreateSetting(user);
        return new NotificationSettingDto(setting);
    }

    @Transactional
    public NotificationSettingDto updateSetting(String email, NotificationSettingDto dto) {
        User user = getUser(email);
        NotificationSetting setting = getOrCreateSetting(user);
        setting.update(
                dto.isFriendRequestEnabled(),
                dto.isSignalEnabled(),
                dto.isReportReminderEnabled(),
                dto.isItemSoldEnabled(),
                dto.isStationJoinEnabled(),
                dto.isNoticeEnabled()
        );
        return new NotificationSettingDto(setting);
    }

    private NotificationSetting getOrCreateSetting(User user) {
        return notificationSettingRepository.findByUser(user)
                .orElseGet(() -> notificationSettingRepository.save(
                        NotificationSetting.builder().user(user).build()
                ));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
