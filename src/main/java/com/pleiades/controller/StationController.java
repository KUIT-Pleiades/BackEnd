package com.pleiades.controller;


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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {

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
}
