package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDto {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @JsonProperty("email")
    private String email;

    @JsonProperty("profile")
    private String profileUrl;
}
