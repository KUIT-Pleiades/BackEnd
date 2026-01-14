package com.pleiades.websocket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SocketSessionService {
    private final RedisTemplate<String, String> redisTemplate;

    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();

    private final Map<String, String> sessionToStation = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> stationSessions = new ConcurrentHashMap<>();

    private final Map<String, Position> positions = new ConcurrentHashMap<>();

    public void registerSession(String sessionId, String userId, String stationId) {
        sessionToUser.put(sessionId, userId);
        sessionToStation.put(sessionId, stationId);
        stationSessions.computeIfAbsent(stationId, k -> ConcurrentHashMap.newKeySet())
                .add(sessionId);
    }

    public void unregisterSession(String sessionId) {
        String stationId = sessionToStation.get(sessionId);

        sessionToUser.remove(sessionId);
        sessionToStation.remove(sessionId);

        if (stationId != null) {
            Set<String> sessions = stationSessions.get(stationId);
            if (sessions != null) {
                sessions.remove(sessionId);
            }
        }
    }

    public String getUserId(String sessionId) {
        return sessionToUser.get(sessionId);
    }

    public String getStationId(String sessionId) {
        return sessionToStation.get(sessionId);
    }

    public void updatePosition(String stationId, String userId, float x, float y) {
        String key = buildPositionKey(stationId, userId);
        positions.put(key, new Position(x, y));
    }

    public Position getPosition(String stationId, String userId) {
        String key = buildPositionKey(stationId, userId);
        return positions.get(key);
    }

    public void savePositionToRedis(String stationId, String userId) {
        String key = buildPositionKey(stationId, userId);
        Position pos = positions.get(key);

        if (pos != null) {
            String redisKey = buildRedisKeyForPositions(stationId);
            String value = pos.x() + "," + pos.y();
            redisTemplate.opsForHash().put(redisKey, userId, value);
        }
    }

    public Map<Object, Object> loadPositionsFromRedis(String stationId) {
        String redisKey = buildRedisKeyForPositions(stationId);
        return redisTemplate.opsForHash().entries(redisKey);
    }

    private String buildRedisKeyForPositions(String stationId) {
        return "station:" + stationId + ":positions";
    }

    private String buildPositionKey(String stationId, String userId) {
        return stationId + ":" + userId;
    }

    public record Position(float x, float y) {}
}
