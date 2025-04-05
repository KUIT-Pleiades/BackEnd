package com.pleiades.dto.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterOutfitDto {
    @NotBlank
    @JsonProperty("top")
    private String topImg;

    @NotBlank
    @JsonProperty("bottom")
    private String bottomImg;

    @NotBlank
    @JsonProperty("shoes")
    private String shoesImg;
}
