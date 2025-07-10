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
    private String head;

    @JsonProperty("eyes_item")
    private String eyesItem;

    @JsonProperty("ears")
    private String ears;

    @JsonProperty("neck")
    private String neck;

    @JsonProperty("leftWrist")
    private String leftWrist;

    @JsonProperty("rightWrist")
    private String rightWrist;

    @JsonProperty("leftHand")
    private String leftHand;

    @JsonProperty("rightHand")
    private String rightHand;

}