package com.pleiades.dto.character.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pleiades.dto.character.CharacterImageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseStarBackgroundDto {
    @JsonProperty("starBackground")
    private List<CharacterImageDto> bgImgs = new ArrayList<>();
}
