package com.pleiades.controller;

import com.pleiades.dto.NotificationResponseDto;
import com.pleiades.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification", description = "알림 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회")
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(HttpServletRequest request) {
        log.info("GET /notifications");
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(notificationService.getNotifications(email));
    }

    @Operation(summary = "알림 전체 읽음 처리")
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(HttpServletRequest request) {
        log.info("PATCH /notifications/read-all");
        String email = (String) request.getAttribute("email");
        notificationService.markAllRead(email);
        return ResponseEntity.noContent().build();
    }
}
