package com.pleiades.dto.naver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NaverLoginResponseDto {

    private String email;
    private String accessToken;

}

