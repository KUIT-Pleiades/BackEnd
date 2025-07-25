package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterDto {

    @NotBlank
    @JsonProperty("face")
    private CharacterFaceDto face;

    @NotBlank
    @JsonProperty("outfit")
    private CharacterOutfitDto outfit;

    @JsonProperty("item")
    private CharacterItemDto item;

    @Pattern(regexp = "^https://gateway\\.pinata\\.cloud/ipfs/.+$")
    @JsonProperty("profile")
    private String profile;

    @Pattern(regexp = "^https://gateway\\.pinata\\.cloud/ipfs/.+$")
    @JsonProperty("character")
    private String character;
}
