package com.pleiades.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FriendListDto {
    private List<FriendDto> received;  // 받은 친구 요청 목록
    private List<FriendDto> friend;    // 내 친구 목록
    private List<FriendDto> sent;      // 보낸 친구 요청 목록
}