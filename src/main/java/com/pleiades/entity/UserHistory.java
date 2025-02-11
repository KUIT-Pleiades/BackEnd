package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 최근 검색 기록 -> 사용자
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int searchCount;

    @Column(nullable = false)
    private boolean isFriend;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_user_id", nullable = false)
    private User currentUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "searched_user_id", nullable = false)
    private User searchedUser;
}
