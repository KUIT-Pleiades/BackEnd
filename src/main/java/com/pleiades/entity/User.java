package com.pleiades.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    // todo : ID user가 직접 입력
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String userName = "임시 사용자";

    @Column(nullable = false)
    private LocalDate birthDate= LocalDate.of(2000, 1, 1);;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Transient
    private String accessToken;

    @Column
    private String refreshToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private NaverToken naverToken;
}
