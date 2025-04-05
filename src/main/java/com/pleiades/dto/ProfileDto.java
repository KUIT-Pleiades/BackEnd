package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDto {
    @NotBlank
    @Pattern(regexp = "^\\w+$", message = "영문자와 숫자만 입력 가능합니다.")
    @Size(min = 4, max = 10)
    @JsonProperty("userId")
    private String userId;

    @NotBlank
    @Pattern(regexp = "^[\\w가-힣]+$", message = "한글, 영문, 숫자만 입력 가능합니다.")
    @Size(min = 2, max = 15)
    @JsonProperty("userName")
    private String userName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @Email
    @JsonProperty("email")
    private String email;

    @Pattern(regexp = "^https://gateway\\.pinata\\.cloud/ipfs/.+$")
    @JsonProperty("profile")
    private String profileUrl;
}