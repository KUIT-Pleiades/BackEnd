package com.pleiades.controller;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.SearchUserDto;
import com.pleiades.entity.Report;
import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.repository.ReportRepository;
import com.pleiades.repository.StationRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.UserStationRepository;
import com.pleiades.service.AuthService;
import com.pleiades.service.ReportService;
import com.pleiades.service.UserService;
import com.pleiades.util.HeaderUtil;

import com.pleiades.dto.station.StationCreateDto;
import com.pleiades.service.StationService;
import com.pleiades.service.UserStationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<List<Report>> checkReport(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization) {
        if (stationId == null || stationId.isEmpty()) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); }
        String email = authService.getEmailByAuthorization(authorization);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "user not found"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Station> station = stationRepository.findById(stationId);
        if (station.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "station not found"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserStationId userStationId = new UserStationId(user.get().getId(), stationId);
        Optional<UserStation> userStation = userStationRepository.findById(userStationId);

        if (userStation.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "user is not in station"));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Report> reports = reportRepository.findByStation(station.get());

        return ResponseEntity.status(HttpStatus.OK).body(reports);
    }
}
