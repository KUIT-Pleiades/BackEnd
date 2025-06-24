package com.pleiades.controller;

import com.pleiades.dto.SignalResponseDto;
import com.pleiades.service.SignalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Signal", description = "시그널 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/friends/signals")
public class SignalController {

    private final SignalService signalService;

    @Operation(summary = "시그널 수신", description = "시그널 받기")
    @GetMapping("")
    public ResponseEntity<Map<String, List<SignalResponseDto>>> getReceivedSignals(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        log.info("시그널: received signals for user: {}", email);
        return signalService.getReceivedSignals(email);
    }

    @Operation(summary = "시그널 송신", description = "시그널 보내기")
    @PostMapping("")
    public ResponseEntity<Map<String, String>> sendSignal(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) {
        String email = (String) request.getAttribute("email");
        String receiverId = requestBody.get("receiverId").toString();
        int imageIndex = (int) requestBody.get("imageIndex");
        log.info("시그널: User {} sending signal to {}", email, receiverId);
        return signalService.sendSignal(email, receiverId, imageIndex);
    }

    @Operation(summary = "시그널 삭제", description = "시그널 삭제하기")
    @DeleteMapping("/{user_id}")
    public ResponseEntity<Map<String, String>> deleteSignal(HttpServletRequest request, @PathVariable("user_id") String userId) {
        String email = (String) request.getAttribute("email");
        log.info("시그널: User {} deleting signal from {}", email, userId);
        return signalService.deleteSignal(email, userId);
    }
}
