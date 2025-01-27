package com.pleiades.dto.character.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pleiades.dto.character.CharacterImageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestCharacterOutfitDto {
    @JsonProperty("top")
    private String topImg;

    @JsonProperty("bottom")
    private String bottomImg;

    @JsonProperty("shoes")
    private String shoesImg;
}
