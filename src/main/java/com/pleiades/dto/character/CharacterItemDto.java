package com.pleiades.dto.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterItemDto {
    @JsonProperty("head")
    private String headImg;

    @JsonProperty("eyes")
    private String eyesImg;

    @JsonProperty("ears")
    private String earsImg;

    @JsonProperty("neck")
    private String neckImg;

    @JsonProperty("leftWrist")
    private String leftWristImg;

    @JsonProperty("rightWrist")
    private String rightWristImg;

    @JsonProperty("leftHand")
    private String leftHandImg;

    @JsonProperty("rightHand")
    private String rightHandImg;

}