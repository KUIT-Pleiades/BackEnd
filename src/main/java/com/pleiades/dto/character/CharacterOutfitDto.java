package com.pleiades.dto.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterOutfitDto {
    @JsonProperty("top")
    private List<CharacterImageDto> topImg = new ArrayList<>();

    @JsonProperty("bottom")
    private List<CharacterImageDto> bottomImg = new ArrayList<>();

    @JsonProperty("shoes")
    private List<CharacterImageDto> shoesImg = new ArrayList<>();
}
