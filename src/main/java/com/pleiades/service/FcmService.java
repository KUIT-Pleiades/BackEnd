package com.pleiades.service;

import com.google.firebase.messaging.*;
import com.pleiades.entity.FcmToken;
import com.pleiades.entity.Notification;
import com.pleiades.entity.NotificationSetting;
import com.pleiades.entity.User;
import com.pleiades.repository.FcmTokenRepository;
import com.pleiades.repository.NotificationRepository;
import com.pleiades.repository.NotificationSettingRepository;
import com.pleiades.strings.NotificationType;
import com.pleiades.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Transactional
    public void send(User receiver, NotificationType type, Long relatedId, Object... args) {
        if (!isEnabled(receiver, type)) {
            return;
        }

        String title = type.getTitle();
        String body = type.formatBody(args);

        saveNotification(receiver, type, title, body, relatedId);
        sendToAllDevices(receiver, title, body);
    }

    private boolean isEnabled(User receiver, NotificationType type) {
        return notificationSettingRepository.findByUser(receiver)
                .map(setting -> isTypeEnabled(setting, type))
                .orElse(true);
    }

    private boolean isTypeEnabled(NotificationSetting setting, NotificationType type) {
        return switch (type) {
            case FRIEND_REQUEST -> setting.isFriendRequestEnabled();
            case FRIEND_ACCEPT -> setting.isFriendRequestEnabled();
            case SIGNAL -> setting.isSignalEnabled();
            case REPORT_REMINDER -> setting.isReportReminderEnabled();
            case ITEM_SOLD -> setting.isItemSoldEnabled();
            case STATION_JOIN -> setting.isStationJoinEnabled();
            case NOTICE -> setting.isNoticeEnabled();
        };
    }

    private void saveNotification(User receiver, NotificationType type, String title, String body, Long relatedId) {
        notificationRepository.save(Notification.builder()
                .receiver(receiver)
                .type(type)
                .title(title)
                .body(body)
                .isRead(false)
                .createdAt(LocalDateTimeUtil.now())
                .relatedId(relatedId)
                .build());
    }

    private void sendToAllDevices(User receiver, String title, String body) {
        List<FcmToken> tokens = fcmTokenRepository.findAllByUser(receiver);
        if (tokens.isEmpty()) {
            return;
        }

        List<String> tokenValues = tokens.stream().map(FcmToken::getToken).toList();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(tokenValues)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            removeInvalidTokens(tokens, response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 발송 실패: {}", e.getMessage());
        }
    }

    private void removeInvalidTokens(List<FcmToken> tokens, BatchResponse response) {
        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                MessagingErrorCode errorCode = responses.get(i).getException().getMessagingErrorCode();
                if (errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                    fcmTokenRepository.deleteByToken(tokens.get(i).getToken());
                    log.info("만료된 FCM 토큰 삭제: {}", tokens.get(i).getToken());
                }
            }
        }
    }
}
