package com.pleiades.websocket.dto;

public record LockResultEvent (
    String targetUserId,
    boolean success,
    String lockedBy
) {}