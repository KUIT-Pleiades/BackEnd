package com.pleiades.controller;

import com.pleiades.dto.ReportDto;
import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.service.AuthService;
import com.pleiades.service.ReportService;
import com.pleiades.strings.ValidationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationReportController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ReportService reportService;
    private final StationRepository stationRepository;
    private final StationReportRepository stationReportRepository;
    private final UserStationRepository userStationRepository;


    @GetMapping("/{stationId}/report")
    public ResponseEntity<Map<String,Object>> checkReport(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization) {
        log.info("/stations/{}/report", stationId);
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        User user = userRepository.findByEmail(email).get();

        Station station = stationRepository.findById(stationId).get();
        Report report = reportService.searchTodaysReport(user, station);

        // 입장할 때 투데이 리포트를 생성했기 때문에 말이 안 되지만 일단 예외 처리를 함
        if (report == null) { throw new CustomException(ErrorCode.USER_NEVER_ENTERED_STATION); }

        ReportDto reportDto = reportService.reportToDto(report);

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.CONTENT_TYPE, "application/json").body(Map.of("report",reportDto));
    }

    @PatchMapping("/{stationId}/report")
    public ResponseEntity<Map<String,Object>> updateReport(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization, @RequestBody Map<String, Object> body) {
        log.info("PATCH /stations/{}/report", stationId);

        String email = authService.getEmailByAuthorization(authorization);

        authService.userInStation(stationId, email);

        User user = userRepository.findByEmail(email).get();

        Station station = stationRepository.findById(stationId).get();

        String answer = body.get("answer").toString();

        ValidationStatus updateReport = reportService.updateTodaysReport(user, station, answer);

        if (updateReport == ValidationStatus.NONE) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Today's report not created"));
        }
        if (updateReport == ValidationStatus.NOT_VALID) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Today's report not created - Same question answered before"));
        }

        Optional<UserStation> userStation = userStationRepository.findByStationIdAndUserId(stationId, user.getId());
        userStation.get().setTodayReport(true);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Today report is written"));
    }

    @GetMapping("/{stationId}/report/create")
    public ResponseEntity<Map<String, Object>> createReport(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization) {
        log.info("/stations/"+stationId+"/report/create");
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        User user = userRepository.findByEmail(email).get();
        Station station = stationRepository.findById(stationId).get();

        Report report = reportService.createReport(user, station);

        if (report == null) { return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); }

        ReportDto reportDto = new ReportDto();
        reportDto.setReportId(report.getId());
        reportDto.setQuestionId(report.getQuestion().getId());
        reportDto.setQuestion(report.getQuestion().getQuestion());
        reportDto.setAnswer(report.getAnswer());
        reportDto.setCreatedAt(report.getCreatedAt());
        reportDto.setModifiedAt(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("report", reportDto));
    }

    @GetMapping("/{stationId}/users/{userId}/report")
    public ResponseEntity<Map<String,Object>> checkUserReport(@PathVariable("stationId") String stationId, @PathVariable("userId") String userId, @RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_FOUND); }

        Station station = stationRepository.findById(stationId).get();
        Question question = reportService.todaysQuestion(station);

        Report report = reportService.searchUserQuestion(user.get(), question);
        if (report == null) { return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "User never responded this question")); }

        Optional<StationReport> stationReport = stationReportRepository.findByStationIdAndReportId(stationId, report.getId());
        if (stationReport.isEmpty()) { return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message","User didn't responded today's report")); }

        ReportDto reportDto = reportService.reportToDto(report);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("report", reportDto));
    }
}
