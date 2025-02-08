package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterDto {
    @JsonProperty("imgPath")
    private String imgPath;

    @JsonProperty("face")
    private CharacterFaceDto face;

    @JsonProperty("outfit")
    private CharacterOutfitDto outfit;

    @JsonProperty("item")
    private CharacterItemDto item;
}
