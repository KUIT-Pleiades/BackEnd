package com.pleiades.service;

import com.pleiades.dto.NotificationResponseDto;
import com.pleiades.entity.Notification;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final SignalRepository signalRepository;
    private final StationRepository stationRepository;

    public List<NotificationResponseDto> getNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return notificationRepository.findAllByReceiverOrderByCreatedAtDesc(user)
                .stream()
                .map(n -> NotificationResponseDto.of(n, resolveRelatedRef(n)))
                .toList();
    }

    @Transactional
    public void markAllRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        notificationRepository.findAllByReceiverOrderByCreatedAtDesc(user)
                .stream()
                .filter(n -> !n.isRead())
                .forEach(Notification::read);
    }

    private String resolveRelatedRef(Notification notification) {
        Long relatedId = notification.getRelatedId();
        if (relatedId == null) return null;

        return switch (notification.getType()) {
            case FRIEND_REQUEST -> friendRepository.findById(relatedId)
                    .map(f -> f.getSender().getId())
                    .orElse(null);
            case FRIEND_ACCEPT -> friendRepository.findById(relatedId)
                    .map(f -> f.getReceiver().getId())
                    .orElse(null);
            case SIGNAL -> signalRepository.findById(relatedId)
                    .map(s -> s.getSender().getId())
                    .orElse(null);
            case STATION_JOIN, REPORT_REMINDER -> stationRepository.findById(relatedId)
                    .map(s -> s.getPublicId().toString())
                    .orElse(null);
            default -> null;
        };
    }
}
