package com.pleiades.entity;

import com.pleiades.dto.SignUpDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    private String id;
    private String nickname;
    private String email;
    private LocalDate birthDate;
    private LocalDate signupDate;

    public void setSignUp(SignUpDto signUpDto) {
        this.setId(signUpDto.getId());
        this.setNickname(signUpDto.getNickname());
        this.setBirthDate(signUpDto.getBirthDate());
        this.setSignupDate(LocalDate.now());
    }
}
