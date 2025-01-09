package com.pleiades.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Character {
    private String userId;
    private int faceId;
    private int clothesId;
}
