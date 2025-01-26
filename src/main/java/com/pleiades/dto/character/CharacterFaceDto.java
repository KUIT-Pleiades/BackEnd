package com.pleiades.dto.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterFaceDto {
    @JsonProperty("skinColor")
    private List<CharacterImageDto> skinImgs = new ArrayList<>();

    @JsonProperty("hair")
    private List<CharacterImageDto> hairImgs = new ArrayList<>();

    @JsonProperty("expression")
    private List<CharacterImageDto> expressionImgs = new ArrayList<>();
}
