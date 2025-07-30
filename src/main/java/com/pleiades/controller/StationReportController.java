package com.pleiades.controller;

import com.pleiades.dto.ReportDto;
import com.pleiades.entity.*;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.report.TodaysReportService;
import com.pleiades.strings.ValidationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Tag(name = "StationReport", description = "오늘의 리포트 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationReportController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final ModelMapper modelMapper;
    private final TodaysReportService todaysReportService;

    @Operation(summary = "투데이리포트 조회", description = "정거장에서 작성한 해당 사용자의 투데이리포트 불러오기")
    @GetMapping("/{stationId}/report")
    public ResponseEntity<Map<String,Object>> checkReport(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization) {
        log.info("/stations/{}/report", stationId);
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        User user = userRepository.findByEmail(email).get();

        Station station = stationRepository.findById(stationId).get();
        Report report = todaysReportService.searchTodaysReport(user, station);

        // 입장할 때 투데이 리포트를 생성했기 때문에 말이 안 되지만 일단 예외 처리를 함
        if (report == null) { throw new CustomException(ErrorCode.USER_NEVER_ENTERED_STATION); }

        ReportDto reportDto = modelMapper.map(report, ReportDto.class);

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.CONTENT_TYPE, "application/json").body(Map.of("report",reportDto));
    }

    @Operation(summary = "투데이리포트 수정", description = "정거장에서 작성한 투데이리프토 수정하기")
    @PatchMapping("/{stationId}/report")
    public ResponseEntity<Map<String,Object>> updateReport(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization, @RequestBody Map<String, Object> body) {
        log.info("PATCH /stations/{}/report", stationId);

        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        User user = userRepository.findByEmail(email).get();
        Station station = stationRepository.findById(stationId).get();

        String answer = body.get("answer").toString();

        ValidationStatus updateReport = todaysReportService.updateTodaysReport(user, station, answer);

        if (updateReport == ValidationStatus.NONE) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Today's report not created"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Today report is written"));
    }

    @Operation(summary = "투데이리포트 작성", description = "정거장에서 투데이리포트 작성하기")
    @GetMapping("/{stationId}/report/create")
    public ResponseEntity<Map<String, Object>> createReport(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization) {
        log.info("/stations/"+stationId+"/report/create");
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        User user = userRepository.findByEmail(email).get();
        Station station = stationRepository.findById(stationId).get();

        Report report = todaysReportService.createTodaysReport(user, station);

        if (report == null) { return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); }

        ReportDto reportDto = modelMapper.map(report, ReportDto.class);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("report", reportDto));
    }

    @Operation(summary = "멤버의 투데이리포트 조회", description = "정거장 멤버의 투데이리포트 불러오기")
    @GetMapping("/{stationId}/users/{userId}/report")
    public ResponseEntity<Map<String,Object>> checkUserReport(@PathVariable("stationId") String stationId, @PathVariable("userId") String userId, @RequestHeader("Authorization") String authorization) {
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_FOUND); }

        Station station = stationRepository.findById(stationId).get();
        Report report = todaysReportService.searchTodaysReport(user.get(), station);

        if (report == null) { return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message","User didn't responded today's report")); }

        ReportDto reportDto = modelMapper.map(report, ReportDto.class);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("report", reportDto));
    }
}
