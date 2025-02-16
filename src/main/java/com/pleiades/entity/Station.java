package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String id; // 정거장 code

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
    private Time reportNoticeTime;

    // TODO: StationBackGround entity 연동
    @Column(nullable = false)
    private String backgroundName;

    // name으로 바꿔야할 듯
    @ManyToOne
    @JoinColumn(name = "background_id")
    StationBackground background;
}
