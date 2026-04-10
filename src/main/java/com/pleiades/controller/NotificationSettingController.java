package com.pleiades.controller;

import com.pleiades.dto.NotificationSettingDto;
import com.pleiades.service.NotificationSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification", description = "알림 설정 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications/settings")
public class NotificationSettingController {

    private final NotificationSettingService notificationSettingService;

    @Operation(summary = "알림 설정 조회")
    @GetMapping
    public ResponseEntity<NotificationSettingDto> getSetting(HttpServletRequest request) {
        log.info("GET /notifications/settings");
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(notificationSettingService.getSetting(email));
    }

    @Operation(summary = "알림 설정 수정")
    @PutMapping
    public ResponseEntity<NotificationSettingDto> updateSetting(HttpServletRequest request,
                                                                @RequestBody NotificationSettingDto dto) {
        log.info("PUT /notifications/settings");
        String email = (String) request.getAttribute("email");
        return ResponseEntity.ok(notificationSettingService.updateSetting(email, dto));
    }
}
