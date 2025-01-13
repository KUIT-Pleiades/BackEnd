package com.pleiades.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    private String id;
    private String nickname;
    private String email;
    private Timestamp birthDate;
    private Timestamp signupDate;

//    public User(String userId, String nickname, Timestamp birthDate, Timestamp signupDate) {
//        this.userId = userId;
//        this.nickname = nickname;
//        this.birthDate = birthDate;
//        this.signupDate = signupDate;
//    }
//
//    public User(String userId, String nickname, Timestamp birthDate) {
//        this.userId = userId;
//        this.nickname = nickname;
//        this.birthDate = birthDate;
//        this.signupDate = null;
//    }
}
