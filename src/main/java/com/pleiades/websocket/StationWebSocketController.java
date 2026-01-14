package com.pleiades.websocket;

import com.pleiades.websocket.dto.*;
import com.pleiades.websocket.service.CharacterLockService;
import com.pleiades.websocket.service.SocketSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StationWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final CharacterLockService lockService;
    private final SocketSessionService sessionService;

    @MessageMapping("/station/{stationId}/dragStart")
    public void handleDragStart(@DestinationVariable String stationId, @Header("simpSessionId") String sessionId, DragStartMessage message) {
        String userId = sessionService.getUserId(sessionId);
        String targetUserId = message.targetUserId();

        boolean acquired = lockService.tryLock(stationId, targetUserId, userId);

        if (acquired) {
            messagingTemplate.convertAndSendToUser(sessionId,"/queue/lockResult", new LockResultEvent(targetUserId, true, null));
            messagingTemplate.convertAndSend("/topic/station/" + stationId, new CharacterLockedEvent(targetUserId, userId));
        }
        else {
            String lockedBy = lockService.getLockedBy(stationId, targetUserId);
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/lockResult", new LockResultEvent(targetUserId, false, lockedBy));
        }
    }

    @MessageMapping("/station/{stationId}/move")
    public void handleMove(@DestinationVariable String stationId, @Header("simpSessionId") String sessionId, PositionMessage message) {
        String userId = sessionService.getUserId(sessionId);
        String targetUserId = message.targetUserId();

        if (!lockService.isLockedBy(stationId, targetUserId, userId)) return;

        sessionService.updatePosition(stationId, targetUserId, message.x(), message.y());

        messagingTemplate.convertAndSend("/topic/station/" + stationId, new UserMovedEvent(targetUserId, userId, message.x(), message.y()));
    }

    @MessageMapping("/station/{stationId}/dragEnd")
    public void handleDragEnd(@DestinationVariable String stationId, @Header("simpSessionId") String sessionId, DragEndMessage message) {
        String userId = sessionService.getUserId(sessionId);
        String targetUserId = message.targetUserId();

        if (!lockService.isLockedBy(stationId, targetUserId, userId)) return;

        lockService.unlock(stationId, targetUserId);

        sessionService.savePositionToRedis(stationId, targetUserId);

        messagingTemplate.convertAndSend("/topic/station/" + stationId, new CharacterUnlockedEvent(targetUserId));
    }



}
