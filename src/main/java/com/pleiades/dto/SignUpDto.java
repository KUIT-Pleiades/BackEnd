package com.pleiades.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pleiades.dto.character.request.RequestCharacterItemDto;
import com.pleiades.dto.character.request.RequestCharacterFaceDto;
import com.pleiades.dto.character.request.RequestCharacterOutfitDto;
import com.pleiades.entity.item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignUpDto {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @JsonProperty("face")
    private RequestCharacterFaceDto face;

    @JsonProperty("outfit")
    private RequestCharacterOutfitDto outfit;

    @JsonProperty("item")
    private Set<Item> item;

//    @JsonProperty("")
//    private Timestamp signupDate;

    @JsonProperty("backgroundId")
    private int backgroundId;
}
