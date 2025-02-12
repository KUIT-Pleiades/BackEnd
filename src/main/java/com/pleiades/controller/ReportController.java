package com.pleiades.controller;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.ReportHistoryDto;
import com.pleiades.entity.ReportHistory;
import com.pleiades.entity.User;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.ReportHistoryRepository;
import com.pleiades.repository.ReportRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.AuthService;
import com.pleiades.service.ReportHistoryService;
import com.pleiades.service.ReportService;
import com.pleiades.service.UserService;
import com.pleiades.strings.FriendStatus;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/reports")
public class ReportController {
    private final ReportHistoryService reportHistoryService;
    AuthService authService;
    UserService userService;
    ReportService reportService;

    JwtUtil jwtUtil;

    UserRepository userRepository;
    ReportRepository reportRepository;
    FriendRepository friendRepository;
    ReportHistoryRepository reportHistoryRepository;

    @Autowired
    public ReportController(AuthService authService, UserService userService, ReportService reportService, JwtUtil jwtUtil, UserRepository userRepository, ReportRepository reportRepository, FriendRepository friendRepository,
                            ReportHistoryService reportHistoryService, ReportHistoryRepository reportHistoryRepository) {
        this.authService = authService;
        this.userService = userService;
        this.reportService = reportService;
        this.reportHistoryService = reportHistoryService;

        this.jwtUtil = jwtUtil;

        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.friendRepository = friendRepository;
        this.reportHistoryRepository = reportHistoryRepository;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> reports(@RequestHeader("Authorization") String authorization) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);

        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ReportDto> reports = reportService.getAllReports(user.get());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("reports", reports));
    }

    @GetMapping(params = "query")
    public ResponseEntity<Object> searchReport(@RequestHeader("Authorization") String authorization, @RequestParam("query") String query) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);
        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        Set<ReportDto> result = reportService.searchResult(user.get(), query);

        // 문자열 중복 검색 후 ReportHistoryRepository 에 저장
        reportHistoryService.saveReportHistory(query, user.get());
        reportHistoryService.deleteIfOverTen(user.get());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("reports", result));
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> searchHistory(@RequestHeader("Authorization") String authorization) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);
        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User is Empty")); }

        List<ReportHistory> histories = reportHistoryRepository.findByUser(user.get());
        log.info("histories: {}", histories);
        List<ReportHistoryDto> historyDtos = new ArrayList<>();
        for (ReportHistory history : histories) {
            log.info("history: {}", history);
            log.info("id: {}", history.getId());
            ReportHistoryDto dto = new ReportHistoryDto();
            dto.setId(history.getId());
            dto.setQuery(history.getQuery());
            dto.setCreatedAt(history.getCreatedAt());
            historyDtos.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("history", historyDtos));
    }

    @DeleteMapping("/history/{historyId}")
    public ResponseEntity<Map<String, Object>> deleteHistory(@RequestHeader("Authorization") String authorization, @PathVariable("historyId") Long historyId) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);
        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User is Empty")); }

        return reportHistoryService.deleteById(historyId);
    }

    @PatchMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> updateReport(@RequestHeader("Authorization") String authorization, @PathVariable("reportId") Long reportId, @RequestBody Map<String, Object> body) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);

        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        String answer = body.get("answer").toString();

        ValidationStatus update = reportService.updateReport(user.get(), reportId, answer);

        if (!update.equals(ValidationStatus.VALID)) { return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build(); }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> deleteReport(@RequestHeader("Authorization") String authorization, @PathVariable("reportId") Long reportId) {
        String accessToken = HeaderUtil.authorizationBearer(authorization);
        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        ValidationStatus delete = reportService.deleteReport(user.get(), reportId);

        if (delete.equals(ValidationStatus.NONE)) { return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(Map.of("message", "No existing report")); }
        if (delete.equals(ValidationStatus.NOT_VALID)) { return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Can't delete Today's report")); }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "report deleted"));
    }

    @GetMapping("/friends")
    public ResponseEntity<Map<String, Object>> friendsReports(@RequestHeader("Authorization") String authorization, @RequestParam("userId") String userId) {
        // 친구 아이디 존재 여부
        Optional<User> friend = userRepository.findById(userId);
        if (friend.isEmpty()) { return ResponseEntity.notFound().build(); }

        // 친구 관계에 있는지 검증
        String accessToken = HeaderUtil.authorizationBearer(authorization);

        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        boolean relationship = friendRepository.isFriend(user.get(), friend.get(), FriendStatus.ACCEPTED);

        if (!relationship) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        List<ReportDto> reports = reportService.getAllReports(friend.get());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("reports", reports));
    }

    @GetMapping(value = "/friends", params = "query")
    public ResponseEntity<Map<String, Object>> searchFriendsReport(@RequestHeader("Authorization") String authorization, @RequestParam("userId") String userId, @RequestParam("query") String query) {
        // 친구 아이디 존재 여부
        Optional<User> friend = userRepository.findById(userId);
        if (friend.isEmpty()) { return ResponseEntity.notFound().build(); }

        // 친구 관계에 있는지 검증
        String accessToken = HeaderUtil.authorizationBearer(authorization);
        Claims token = jwtUtil.validateToken(accessToken);
        String email = token.getSubject();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        boolean relationship = friendRepository.isFriend(user.get(), friend.get(), FriendStatus.ACCEPTED);

        if (!relationship) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        // report 검색
        Set<ReportDto> result = reportService.searchResult(friend.get(), query);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("reports", result));
    }
}
