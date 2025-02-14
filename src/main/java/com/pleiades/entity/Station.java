package com.pleiades.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(nullable = false)
    private String backgroundName; // stationBackground와 연동 필요
}
