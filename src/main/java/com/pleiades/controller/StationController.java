package com.pleiades.controller;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.SearchUserDto;
import com.pleiades.dto.station.StationSettingDto;
import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
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

    // todo: dto 반영
    @GetMapping("/{stationId}/report")
    public ResponseEntity<Map<String, Object>> checkReport(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization) {
        log.info("/stations/{}/report", stationId);
        ResponseEntity<Map<String, Object>> response = authService.userInStation(stationId, authorization);
        if (response != null) { return response; }

        List<StationQuestion> stationQuestions = stationQuestionRepository.findByStationId(stationId);
        List<UserStation> usersInStation = userStationRepository.findByStationId(stationId);
        List<ReportDto> reportDtos = new ArrayList<>();

        for (StationQuestion stationQuestion : stationQuestions) {
            Question question = stationQuestion.getQuestion();
            for (UserStation userInStation : usersInStation) {
                Report report = reportService.searchUserQuestion(userInStation.getUser(), question);
                ReportDto reportDto = new ReportDto();
                reportDto.setReportId(report.getId());
                reportDto.setReportId(report.getQuestion().getId());
                reportDto.setQuestion(report.getQuestion().getQuestion());
                reportDto.setAnswer(report.getAnswer());
                reportDto.setCreatedAt(report.getCreatedAt());
                reportDto.setModifiedAt(report.getModifiedAt());
                reportDtos.add(reportDto);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("report", reportDtos));
    }

    @PostMapping("/{stationId}/report")
    public ResponseEntity<Map<String,String>> submitReport(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization, @RequestBody Map<String, Object> body) {
        log.info("/stations/{}/report", stationId);
        if (stationId == null || stationId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "user not found")); }

        Optional<Station> station = stationRepository.findById(stationId);
        if (station.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "station not found")); }

        UserStationId userStationId = new UserStationId(user.get().getId(), stationId);
        Optional<UserStation> userStation = userStationRepository.findById(userStationId);

        if (userStation.isEmpty()) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "user is not in station")); }

        String answer = body.get("answer").toString();

        Report todayReport = reportService.searchTodaysReport(user.get(), stationId);

        if (todayReport == null) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "report not found")); }

        todayReport.setAnswer(answer);
        todayReport.setModifiedAt(LocalDateTime.now());
        reportRepository.save(todayReport);

        userStation.get().setTodayReport(true);
        userStationRepository.save(userStation.get());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Today report is written"));
    }

    @PatchMapping("/{station_id}/background")
    public ResponseEntity<Map<String, Object>> updateBackground(@PathVariable("station_id") String stationId, @RequestHeader("Authorization") String authorization, @RequestBody Map<String, Object> body) {
        log.info("/stations/"+stationId+"/background");
        ResponseEntity<Map<String, Object>> response = authService.userInStation(stationId, authorization);
        if (response != null) { return response; }

        String backgroundName = body.get("backgroundName").toString();
        log.info("backgroundName: " + backgroundName);

        ValidationStatus setBackground = stationService.setBackground(stationId, backgroundName);

        // station 없음
        if (setBackground == ValidationStatus.NONE) { return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); }

        // background 없음
        if (setBackground == ValidationStatus.NOT_VALID) { return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Background editted"));
    }

    @PatchMapping("/{stationId}/settings")
    public ResponseEntity<Map<String, Object>> stationSetting(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization, @RequestBody StationSettingDto settingDto) {
        log.info("/stations/"+stationId+"/settings");
        ResponseEntity<Map<String, Object>> response = authService.userInStation(stationId, authorization);
        if (response != null) { return response; }

        ValidationStatus setStation = stationService.stationSettings(stationId, settingDto);

        if (setStation == ValidationStatus.NONE) { return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Info editted"));
    }
}