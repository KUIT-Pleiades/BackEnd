package com.pleiades.dto.character.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestCharacterFaceDto {
    @JsonProperty("skinColor")
    private String skinImg;

    @JsonProperty("hair")
    private String hairImg;

    @JsonProperty("expression")
    private String expressionImg;
}
