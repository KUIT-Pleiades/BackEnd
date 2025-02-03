package com.pleiades.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCBResponseDto {

    private String refreshToken;
    private String accessToken;

}

