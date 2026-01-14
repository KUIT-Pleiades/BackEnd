package com.pleiades.websocket.dto;

import java.util.List;

public record StationMembersEvent(
        List<MemberPosition> members
) {
    public record MemberPosition(
            String userId,
            float x,
            float y
    ) {}
}
