package com.pleiades.websocket;

import com.pleiades.service.station.UserStationService;
import com.pleiades.websocket.dto.StationMembersEvent;
import com.pleiades.websocket.dto.UserJoinedEvent;
import com.pleiades.websocket.dto.UserLeftEvent;
import com.pleiades.websocket.service.CharacterLockService;
import com.pleiades.websocket.service.SocketSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SocketSessionService sessionService;
    private final CharacterLockService lockService;
    private final UserStationService userStationService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        String userId = accessor.getFirstNativeHeader("userId");
        String stationId = accessor.getFirstNativeHeader("stationId");

        if (userId == null || stationId == null) {
            log.warn("Missing userId or station id in connection request");
            return;
        }

        if (!userStationService.isMember(stationId, userId)) {
            log.warn("User {} is not member of station {}", userId, stationId);
            return;
        }

        sessionService.registerSession(sessionId, userId, stationId);
        log.info("User {} connected to Station {}", userId, stationId);

        Map<Object, Object> redisPositions = sessionService.loadPositionsFromRedis(stationId);
        List<StationMembersEvent.MemberPosition> memberPositions = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : redisPositions.entrySet()) {
            String memberId = (String) entry.getKey();
            String posStr = (String) entry.getValue();
            String[] parts = posStr.split(",");

            float x = Float.parseFloat(parts[0]);
            float y = Float.parseFloat(parts[1]);

            SocketSessionService.Position livePos = sessionService.getPosition(stationId, memberId);
            if (livePos != null) {
                x = livePos.x();
                y = livePos.y();
            }

            memberPositions.add(new StationMembersEvent.MemberPosition(memberId, x, y));
        }

        messagingTemplate.convertAndSendToUser(sessionId, "/queue/stationMembers", new StationMembersEvent(memberPositions));
        messagingTemplate.convertAndSend("/topic/station/" + stationId, new UserJoinedEvent(userId));

    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        String userId = sessionService.getUserId(sessionId);
        String stationId = sessionService.getStationId(sessionId);

        if (userId == null || stationId == null) {
            log.warn("Missing userId or station id in disconnect request");
            return;
        }

        log.info("User {} disconnected from Station {}", userId, stationId);

        lockService.releaseAllLocksByUser(userId);

        sessionService.unregisterSession(sessionId);

        messagingTemplate.convertAndSend(
                "/topic/station" + stationId,
                new UserLeftEvent(userId)
        );
    }

}
