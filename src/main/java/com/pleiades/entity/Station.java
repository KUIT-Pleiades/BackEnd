package com.pleiades.entity;

import com.pleiades.entity.character.TheItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID publicId;

    @Column(nullable = false)
    private String name;

    @Column
    private String intro;

    @Column(nullable = false)
    private int numberOfUsers;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String adminUserId;

    @Column(nullable = false)
    private LocalTime reportNoticeTime;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime recentActivity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_id")
    TheItem background;

    @ManyToOne
    @JoinColumn(name = "bgOwner_id", nullable = true)
    User backgroundOwner;

    @PrePersist
    protected void generateUuid() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

    public void updateRecentActivity(LocalDateTime recentActivity) {
        this.recentActivity = recentActivity;
    }
}
