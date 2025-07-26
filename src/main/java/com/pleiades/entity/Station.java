package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private String id; // 정거장 code와 별개

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

    // name으로 바꿔야할 듯 - 왜 이렇게 생각했을까
    @ManyToOne
    @JoinColumn(name = "background_id")
    StationBackground background;
}
