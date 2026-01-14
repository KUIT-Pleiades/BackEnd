package com.pleiades.websocket.dto;

public record PositionMessage(
        String targetUserId,
        float x,
        float y
) {}
