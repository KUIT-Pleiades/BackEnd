package com.pleiades.websocket.dto;

public record CharacterLockedEvent (
    String targetUserId,
    String userId
) {}