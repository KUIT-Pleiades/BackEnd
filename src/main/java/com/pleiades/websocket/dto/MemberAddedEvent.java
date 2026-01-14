package com.pleiades.websocket.dto;

public record MemberAddedEvent(
        String userId,
        float x,
        float y
) {}
