package com.pleiades.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendDto {
    private Long friendId;
    private String userId;
    private String userName;
    private String profile;
}