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
    private String skinColor;

    @NotBlank
    @JsonProperty("hair")
    private String hair;

    @NotBlank
    @JsonProperty("eyes")
    private String eyes;

    @NotBlank
    @JsonProperty("nose")
    private String nose;

    @NotBlank
    @JsonProperty("mouth")
    private String mouth;

    @JsonProperty("mole")
    private String mole;
}
