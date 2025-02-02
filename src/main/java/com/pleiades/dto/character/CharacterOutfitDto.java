package com.pleiades.dto.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterOutfitDto {
    @JsonProperty("top")
    private String topImg;

    @JsonProperty("bottom")
    private String bottomImg;

    @JsonProperty("shoes")
    private String shoesImg;
}
