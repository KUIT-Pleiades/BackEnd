package com.pleiades.dto.character.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestCharacterItemDto {
    @JsonProperty("items")
    private List<String> itemImgs = new ArrayList<>();
}

// 쓸 일이 있으려나