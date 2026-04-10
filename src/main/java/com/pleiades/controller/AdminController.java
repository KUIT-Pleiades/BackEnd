package com.pleiades.controller;

import com.pleiades.dto.NoticeRequestDto;
import com.pleiades.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Admin", description = "관리자 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final FcmService fcmService;

    // TODO: [AUTH-3] 관리자 인증 취약점 수정 후 적절한 인증 수단 적용 필요
    @Operation(summary = "공지사항 발송", description = "전체 유저에게 FCM 푸시 알림 발송")
    @PostMapping("/notice")
    public ResponseEntity<Map<String, String>> sendNotice(@RequestBody NoticeRequestDto dto) {
        log.info("/admin/notice");
        fcmService.broadcast(dto.getBody());
        return ResponseEntity.ok(Map.of("message", "공지사항 발송 완료"));
    }
}
