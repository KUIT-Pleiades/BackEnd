package com.pleiades.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NaverLoginRequest {
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String code;
    private String state;
}
