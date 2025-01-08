package com.pleiades.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

// todo: responseDTO 생성자 재구성
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NaverLoginResponse {

    private String naverId;
    private String email;
    private String birthDate;
    private String userName;

    // Nullable
    private String phoneNumber;
    private String address;
    private String profileImage;

    private String accessToken;

    public NaverLoginResponse(String userName, String naverId, String birthDate) {
        this.userName = userName;
        this.naverId = naverId;
        this.birthDate = birthDate;
    }
}

