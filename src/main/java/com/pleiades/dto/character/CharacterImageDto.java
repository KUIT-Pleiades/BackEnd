package com.pleiades.dto.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 프론트 url 안 보내 주는 걸로 수정함
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterImageDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;
}
