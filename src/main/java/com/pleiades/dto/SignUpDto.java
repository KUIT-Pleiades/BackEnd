package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pleiades.dto.character.request.RequestCharacterFaceDto;
import com.pleiades.dto.character.request.RequestCharacterOutfitDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignUpDto {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("birthdate")
    private LocalDate birthdate;

    @JsonProperty("face")
    private RequestCharacterFaceDto face;

    @JsonProperty("outfit")
    private RequestCharacterOutfitDto outfit;

    @JsonProperty("item")
    private Set<String> item;

    @JsonProperty("backgroundId")
    private int backgroundId;
}
