package com.pleiades.dto.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterFaceDto {
    @NotBlank
    @JsonProperty("skinColor")
    private String skinImg;

    @NotBlank
    @JsonProperty("hair")
    private String hairImg;

    @NotBlank
    @JsonProperty("expression")
    private String expressionImg;
}
