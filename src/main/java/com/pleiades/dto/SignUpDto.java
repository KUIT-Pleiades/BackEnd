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
public class SignUpDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("nickname")
    private String nickname;

//    @JsonProperty("")
//    private String email;

    @JsonProperty("birthDate")
    private LocalDate birthDate;

//    @JsonProperty("")
//    private Timestamp signupDate;

    @JsonProperty("backgroundId")
    private int backgroundId;

    @JsonProperty("face")
    private CharacterFaceDto face;

    @JsonProperty("outfit")
    private CharacterOutfitDto outfit;

    @JsonProperty("item")
    private CharacterItemDto item;

    // 악세사리
}
