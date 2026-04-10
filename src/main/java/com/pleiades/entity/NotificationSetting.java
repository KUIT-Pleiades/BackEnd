package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private boolean friendRequestEnabled = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean signalEnabled = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean reportReminderEnabled = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean itemSoldEnabled = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean stationJoinEnabled = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean noticeEnabled = true;
}
