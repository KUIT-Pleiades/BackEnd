package com.pleiades.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class User {
    private String userId;
    private String nickname;
    private final Timestamp birthDate;
    private final Timestamp signupDate;

    public User(String userId, String nickname, Timestamp birthDate, Timestamp signupDate) {
        this.userId = userId;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.signupDate = signupDate;
    }

    public User(String userId, String nickname, Timestamp birthDate) {
        this.userId = userId;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.signupDate = null;
    }
}
