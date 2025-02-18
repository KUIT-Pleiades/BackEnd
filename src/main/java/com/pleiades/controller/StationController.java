package com.pleiades.controller;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.SearchUserDto;
import com.pleiades.dto.station.StationSettingDto;
import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.service.AuthService;
import com.pleiades.service.ReportService;
import com.pleiades.service.UserService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;

import com.pleiades.dto.station.StationCreateDto;
import com.pleiades.service.StationService;
import com.pleiades.service.UserStationService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {

    private final UserService userService;
    private final AuthService authService;

    private final StationRepository stationRepository;
    private final UserStationRepository userStationRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final StationService stationService;
    private final QuestionRepository questionRepository;
    private final StationQuestionRepository stationQuestionRepository;
    private final StationBackgroundRepository stationBackgroundRepository;

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createStation(HttpServletRequest request, @RequestBody StationCreateDto requestDto) {
        log.info("createStation controller 진입");

        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        log.info("사용자 email = {}", email);

        Map<String, Object> response = stationService.createStation(email, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{station_id}")
    public ResponseEntity<Map<String, String>> deleteStation(HttpServletRequest request, @PathVariable("station_id") String station_id) {
        log.info("deleteStation controller 진입");

        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        log.info("사용자 email = {}", email);

        return stationService.deleteStation(email, station_id);
    }

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
        userStation.get().setTodayReport(true);     // todo: false로 전환 필요

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Today report is written"));

    }

    @PatchMapping("/{station_id}/background")
    public ResponseEntity<Map<String, Object>> updateBackground(@PathVariable("station_id") String stationId, @RequestHeader("Authorization") String authorization, @RequestBody Map<String, Object> body) {
        log.info("/stations/"+stationId+"/background");
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        String backgroundName = body.get("backgroundName").toString();
        log.info("backgroundName: " + backgroundName);

        Station station = stationRepository.findById(stationId).get();
        ValidationStatus setBackground = stationService.setBackground(station, backgroundName);

        // background 없음
        if (setBackground == ValidationStatus.NOT_VALID) { throw new CustomException(ErrorCode.IMAGE_NOT_FOUND); }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Background editted"));
    }

    @PatchMapping("/{stationId}/settings")
    public ResponseEntity<Map<String, Object>> stationSetting(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization, @RequestBody StationSettingDto settingDto) {
        log.info("/stations/"+stationId+"/settings");
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        Station station  = stationRepository.findById(stationId).get();
        stationService.stationSettings(station, settingDto);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Info editted"));
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
}