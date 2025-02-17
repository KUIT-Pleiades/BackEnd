package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDto {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("userName")
    private String userName;

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
