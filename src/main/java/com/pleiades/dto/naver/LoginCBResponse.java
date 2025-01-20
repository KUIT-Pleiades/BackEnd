package com.pleiades.dto.naver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCBResponse {

    private String refreshToken;
    private String accessToken;

}

