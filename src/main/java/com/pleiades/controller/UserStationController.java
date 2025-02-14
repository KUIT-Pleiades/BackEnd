package com.pleiades.controller;

import com.pleiades.dto.station.StationHomeDto;
import com.pleiades.dto.station.StationListDto;
import com.pleiades.service.StationService;
import com.pleiades.service.UserStationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/stations")
public class UserStationController {

    private final UserStationService userStationService;

    @GetMapping("")
    public ResponseEntity<StationListDto> getStationList(HttpServletRequest request) {
        log.info("station List 출력 Controller 진입");

        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 401

        log.info("사용자 email = {}", email);

        StationListDto response = userStationService.getStationList(email);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{station_id}")
    public ResponseEntity<StationHomeDto> addUserStation(@PathVariable("station_id") String stationId, HttpServletRequest request) {
        log.info("멤버 추가: add UserStation Controller 진입");
        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 401
        log.info("사용자 email = {}", email);

        StationHomeDto stationHomeDto = userStationService.addUserToStation(email, stationId);

        return ResponseEntity.ok(stationHomeDto);
    }
}
