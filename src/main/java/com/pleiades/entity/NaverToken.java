package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "naver_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaverToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String email;

    @Transient
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    private long lastUpdated;
}
