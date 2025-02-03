package com.pleiades.entity;

import com.pleiades.dto.SignUpDto;
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

    @Id
    private String id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String userName;

    @Column
    private LocalDate birthDate;

    @Column
    private LocalDate createdDate;

    // DB에 앱 자체 tokens 저장 X
    @Transient
    private String accessToken;

    @Column
    private String refreshToken;

    // todo: cascade 설정
    @OneToOne(mappedBy = "user")
    private NaverToken naverToken;

    @Column(nullable = true)
    String imgPath;
}