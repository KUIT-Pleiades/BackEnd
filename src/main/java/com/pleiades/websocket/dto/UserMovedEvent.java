package com.pleiades.websocket.dto;

public record UserMovedEvent(
        String targetUserId,
        String movedBy,
        float x,
        float y
) {}