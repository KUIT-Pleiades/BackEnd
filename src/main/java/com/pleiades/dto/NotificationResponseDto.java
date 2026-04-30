package com.pleiades.dto;

import com.pleiades.entity.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {
    private final Long id;
    private final String type;
    private final String title;
    private final String body;
    private final boolean isRead;
    private final LocalDateTime createdAt;
    private final String relatedRef;

    private NotificationResponseDto(Notification notification, String relatedRef) {
        this.id = notification.getId();
        this.type = notification.getType().name();
        this.title = notification.getTitle();
        this.body = notification.getBody();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
        this.relatedRef = relatedRef;
    }

    public static NotificationResponseDto of(Notification notification, String relatedRef) {
        return new NotificationResponseDto(notification, relatedRef);
    }
}
