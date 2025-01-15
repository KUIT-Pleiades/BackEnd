package com.pleiades.dto.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterFaceDto {
    @JsonProperty("skinColor")
    private CharacterImageDto skinImg;

    @JsonProperty("hair")
    private CharacterImageDto hairImg;

    @JsonProperty("expression")
    private CharacterImageDto expressionImg;
}
