package com.pleiades.websocket.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CharacterLockService {

    private final Map<String, LockInfo> locks = new ConcurrentHashMap<>();

    private static final long LOCK_TIMEOUT_MS = 30000; // 30초

    public boolean tryLock(String stationId, String targetUserId, String lockedBy) {
        String key = buildKey(stationId, targetUserId);
        LockInfo existing = locks.get(key);

        if (existing != null && !existing.lockedBy.equals(lockedBy)) {
            return false;
        }

        locks.put(key, new LockInfo(lockedBy, System.currentTimeMillis()));
        return true;
    }

    public void unlock(String stationId, String targetUserId) {
        String key = buildKey(stationId, targetUserId);
        locks.remove(key);
    }

    public boolean isLockedBy(String stationId, String targetUserId, String userId) {
        String key = buildKey(stationId, targetUserId);
        LockInfo lock = locks.get(key);
        return lock != null && lock.lockedBy().equals(userId);
    }

    public String getLockedBy(String stationId, String targetUserId) {
        String key = buildKey(stationId, targetUserId);
        LockInfo lock = locks.get(key);
        return lock != null ? lock.lockedBy() : null;
    }

    public void releaseAllLocksByUser(String userId) {
        locks.entrySet().removeIf(entry -> entry.getValue().lockedBy().equals(userId));
    }

    @Scheduled(fixedRate = 10000) // 10초
    public void cleanUpExpiredLocks() {
        long now = System.currentTimeMillis();
        locks.entrySet().removeIf(entry ->
                now - entry.getValue().lockedAt() > LOCK_TIMEOUT_MS
        );
    }

    private String buildKey(String stationId, String targetUserId) {
        return stationId + ":" + targetUserId;
    }

    private record LockInfo(String lockedBy, long lockedAt) {}
}
