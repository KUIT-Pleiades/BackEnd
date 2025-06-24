package com.pleiades.controller;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.ReportHistoryDto;
import com.pleiades.entity.ReportHistory;
import com.pleiades.entity.User;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.ReportHistoryRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.report.ReportHistoryService;
import com.pleiades.service.report.ReportService;
import com.pleiades.service.report.SearchReportService;
import com.pleiades.strings.FriendStatus;
import com.pleiades.strings.ValidationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Report", description = "리포트 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {
    private final ReportHistoryService reportHistoryService;
    private final AuthService authService;
    private final ReportService reportService;
    private final SearchReportService searchReportService;

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final ReportHistoryRepository reportHistoryRepository;

    private final ModelMapper modelMapper;

    @Operation(summary = "리포트 불러오기", description = "리포트 불러오기")
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> reports(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        log.info("사용자 email = {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","no user")); }

        List<ReportDto> reports = reportService.getAllReports(user.get());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("reports", reports));
    }

    @Operation(summary = "리포트 조회", description = "리포트 검색하기")
    @GetMapping(params = "query")
    public ResponseEntity<Object> searchReport(@RequestHeader("Authorization") String authorization, @RequestParam("query") String query) {
        String email = authService.getEmailByAuthorization(authorization);
        log.info("사용자 email = {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no user")); }

        Set<ReportDto> result = searchReportService.searchResult(user.get(), query);

        // 문자열 중복 검색 후 ReportHistoryRepository 에 저장
        reportHistoryService.saveReportHistory(query, user.get());
        reportHistoryService.deleteIfOverTen(user.get());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("reports", result));
    }

    @Operation(summary = "리포트 검색 기록", description = "리포트 검색 기록 불러오기")
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> searchHistory(@RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        log.info("사용자 email = {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no user")); }

        List<ReportHistory> histories = reportHistoryRepository.findByUserOrderByCreatedAtDesc(user.get());
        log.info("histories: {}", histories);
        List<ReportHistoryDto> historyDtos = new ArrayList<>();
        for (ReportHistory history : histories) {
            log.info("history: {}", history);
            log.info("id: {}", history.getId());

            ReportHistoryDto dto = modelMapper.map(history, ReportHistoryDto.class);

            historyDtos.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("history", historyDtos));
    }

    @Operation(summary = "리포트 검색 기록 삭제", description = "리포트 검색 기록 삭제")
    @DeleteMapping("/history/{historyId}")
    public ResponseEntity<Map<String, Object>> deleteHistory(@RequestHeader("Authorization") String authorization, @PathVariable("historyId") Long historyId) {
        String email = authService.getEmailByAuthorization(authorization);
        log.info("사용자 email = {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no user")); }

        return reportHistoryService.deleteById(historyId);
    }

    @Operation(summary = "리포트 수정", description = "리포트 수정하기")
    @PatchMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> updateReport(@RequestHeader("Authorization") String authorization, @PathVariable("reportId") Long reportId, @RequestBody Map<String, Object> body) {
        String email = authService.getEmailByAuthorization(authorization);
        log.info("사용자 email = {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "no user")); }

        String answer = body.get("answer").toString();
        ValidationStatus update = reportService.updateReport(user.get(), reportId, answer);

        if (update.equals(ValidationStatus.NONE)) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","no report")); }
        if (update.equals(ValidationStatus.NOT_VALID)) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","not the owner of the report")); }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("report", "report editted"));
    }

    @Operation(summary = "리포트 삭제", description = "리포트 삭제하기")
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> deleteReport(@RequestHeader("Authorization") String authorization, @PathVariable("reportId") Long reportId) {
        String email = authService.getEmailByAuthorization(authorization);
        log.info("사용자 email = {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        ValidationStatus delete = reportService.deleteReport(user.get(), reportId);

        if (delete.equals(ValidationStatus.NONE)) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existing report")); }
        if (delete.equals(ValidationStatus.NOT_VALID)) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Not the owner of the report")); }
        if (delete.equals(ValidationStatus.DUPLICATE)) { return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Can't delete Today's report")); }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "report deleted"));
    }

    @Operation(summary = "친구 리포트", description = "친구 리포트 불러오기")
    @GetMapping("/friends")
    public ResponseEntity<Map<String, Object>> friendsReports(@RequestHeader("Authorization") String authorization, @RequestParam("userId") String userId) {
        // 친구 아이디 존재 여부
        Optional<User> friend = userRepository.findById(userId);
        if (friend.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "user not found")); }

        // 친구 관계에 있는지 검증
        String email = authService.getEmailByAuthorization(authorization);
        log.info("사용자 email = {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "need sign-up")); }

        boolean relationship = friendRepository.isFriend(user.get(), friend.get(), FriendStatus.ACCEPTED);

        if (!relationship) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","not friend with user")); }

        List<ReportDto> reports = reportService.getAllReports(friend.get());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("reports", reports));
    }

    @Operation(summary = "친구 리포트 조회", description = "친구 리포트 검색하기")
    @GetMapping(value = "/friends", params = "query")
    public ResponseEntity<Map<String, Object>> searchFriendsReport(@RequestHeader("Authorization") String authorization, @RequestParam("userId") String userId, @RequestParam("query") String query) {
        // 친구 아이디 존재 여부
        Optional<User> friend = userRepository.findById(userId);
        if (friend.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "user not found")); }

        // 친구 관계에 있는지 검증
        String email = authService.getEmailByAuthorization(authorization);
        log.info("사용자 email = {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "need sign-up")); }

        boolean relationship = friendRepository.isFriend(user.get(), friend.get(), FriendStatus.ACCEPTED);

        if (!relationship) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","not friend with user")); }

        // report 검색
        Set<ReportDto> result = searchReportService.searchResult(friend.get(), query);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("reports", result));
    }
}
