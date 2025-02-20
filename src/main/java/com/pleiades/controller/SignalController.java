package com.pleiades.controller;

import com.pleiades.service.SignalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/friends/signals")
public class SignalController {

    private final SignalService signalService;

    @GetMapping("")
    public ResponseEntity<List<Map<String, String>>> getReceivedSignals(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        log.info("시그널: received signals for user: {}", email);
        return ResponseEntity.ok(signalService.getReceivedSignals(email));
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> sendSignal(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) {
        String email = (String) request.getAttribute("email");
        String receiverId = requestBody.get("receiverId").toString();
        log.info("시그널: User {} sending signal to {}", email, receiverId);
        return signalService.sendSignal(email, receiverId);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Map<String, String>> deleteSignal(HttpServletRequest request, @PathVariable("user_id") String userId) {
        String email = (String) request.getAttribute("email");
        log.info("시그널: User {} deleting signal from {}", email, userId);
        return signalService.deleteSignal(email, userId);
    }
}
