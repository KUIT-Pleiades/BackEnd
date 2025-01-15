package com.pleiades.dto.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterOutfitDto {
    @JsonProperty("top")
    private CharacterImageDto topImg;

    @JsonProperty("bottom")
    private CharacterImageDto bottomImg;

    @JsonProperty("shoes")
    private CharacterImageDto shoesImg;
}
