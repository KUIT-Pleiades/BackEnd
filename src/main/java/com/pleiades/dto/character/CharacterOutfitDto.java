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
    private String top;

    @NotBlank
    @JsonProperty("bottom")
    private String bottom;

    @NotBlank
    @JsonProperty("shoes")
    private String shoes;

    @NotBlank
    @JsonProperty("set")
    private String set;
}
