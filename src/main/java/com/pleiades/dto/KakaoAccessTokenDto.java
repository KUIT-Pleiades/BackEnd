package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAccessTokenDto {
    @JsonProperty("id")
    private long id;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("app_id")
    private int appId;
}
