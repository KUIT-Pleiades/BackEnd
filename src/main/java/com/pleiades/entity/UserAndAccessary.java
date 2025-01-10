package com.pleiades.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserAndAccessary {
    private String userId;
    private int accessaryId;
}
