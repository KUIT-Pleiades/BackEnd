package com.pleiades.controller;

import com.pleiades.dto.station.StationSettingDto;
import com.pleiades.entity.*;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.service.AuthService;
import com.pleiades.service.UserService;
import com.pleiades.strings.ValidationStatus;

import com.pleiades.dto.station.StationCreateDto;
import com.pleiades.service.StationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {

    private final UserService userService;
    private final AuthService authService;

    private final StationRepository stationRepository;
    private final StationService stationService;

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createStation(HttpServletRequest request, @Valid @RequestBody StationCreateDto requestDto) {
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

    @PatchMapping("/{station_id}/background")
    public ResponseEntity<Map<String, Object>> updateBackground(@PathVariable("station_id") String stationId, @RequestHeader("Authorization") String authorization, @RequestBody Map<String, Object> body) {
        log.info("/stations/"+stationId+"/background");
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        Object stationBackground = body.get("stationBackground");
        if (stationBackground == null) { return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(Map.of("message","Station Background required.")); }
        String background = stationBackground.toString();
        log.info("stationBackground: " + background);

        Station station = stationRepository.findById(stationId).get();
        ValidationStatus setBackground = stationService.setBackground(station, background);

        // background 없음
        if (setBackground == ValidationStatus.NOT_VALID) { throw new CustomException(ErrorCode.IMAGE_NOT_FOUND); }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Background edited"));
    }

    @PatchMapping("/{stationId}/settings")
    public ResponseEntity<Map<String, Object>> stationSetting(@PathVariable("stationId") String stationId, @RequestHeader("Authorization") String authorization, @Valid @RequestBody StationSettingDto settingDto) {
        log.info("/stations/"+stationId+"/settings");
        String email = authService.getEmailByAuthorization(authorization);
        authService.userInStation(stationId, email);

        Station station  = stationRepository.findById(stationId).get();
        stationService.stationSettings(station, settingDto);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Info editted"));
    }

}