package com.pleiades.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchUserDto {
    private String userId;
    private String userName;
    private String profile;
    private boolean isFriend;
}

