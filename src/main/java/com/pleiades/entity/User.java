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

    // todo : ID user가 직접 입력
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


//    @Column(unique = true, nullable = false)
    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Transient
    private String accessToken;

    @Column
    private String refreshToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private NaverToken naverToken;

    public void setSignUp(SignUpDto signUpDto) {
        this.setId(signUpDto.getId());
        this.setUserName(signUpDto.getNickname());
        this.setBirthDate(signUpDto.getBirthDate());
        this.setCreatedDate(LocalDate.now());
    }
}