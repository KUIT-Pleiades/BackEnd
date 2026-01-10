package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDto {
    @Pattern(regexp = "^\\w+$", message = "영문자와 숫자만 입력 가능합니다.")
    @JsonProperty("userId")
    private String userId;

    @Pattern(regexp = "^[\\w가-힣]+$", message = "한글, 영문, 숫자만 입력 가능합니다.")
    @JsonProperty("userName")
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @JsonProperty("starBackground")
    private String backgroundName;

    @JsonProperty("profile")
    private String profile;

    @JsonProperty("character")
    private String character;

    @JsonProperty("face")
    private CharacterFaceDto face;

    @JsonProperty("outfit")
    private CharacterOutfitDto outfit;

    @JsonProperty("item")
    private CharacterItemDto item;
}
