package com.pleiades.controller;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.station.StationHomeDto;
import com.pleiades.dto.station.StationListDto;
import com.pleiades.dto.station.UserPositionDto;
import com.pleiades.entity.*;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.StationQuestionRepository;
import com.pleiades.repository.StationReportRepository;
import com.pleiades.repository.StationRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.AuthService;
import com.pleiades.service.ReportService;
import com.pleiades.service.UserStationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/stations")
public class UserStationController {

    private final UserStationService userStationService;

    @PatchMapping("/{station_id}/users/{user_id}/position")
    public ResponseEntity<Map<String, String>> setUserPosition(HttpServletRequest request, @RequestBody UserPositionDto requestBody, @PathVariable String station_id, @PathVariable String user_id){
        log.info("setUserPosition controller 진입");

        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        log.info("사용자 email = {}", email);

        Map<String, String> response = userStationService.setUserPosition(email, station_id, user_id, requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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
    public ResponseEntity<Map<String, String>> addUserStation(@PathVariable("station_id") String stationId, HttpServletRequest request) {
        log.info("멤버 추가: add UserStation Controller 진입");
        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 401
        log.info("사용자 email = {}", email);

        Map<String, String> response = userStationService.addMemberToStation(email, stationId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{station_id}")
    public ResponseEntity<StationHomeDto> enterStation(@PathVariable("station_id") String stationId, HttpServletRequest request) {
        log.info("정거장 입장 Controller 진입");
        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 401
        log.info("사용자 email = {}", email);

        StationHomeDto stationHomeDto = userStationService.enterStation(email, stationId);

        if(!stationHomeDto.isReportWritten()){ // 202
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(stationHomeDto);
        }
        return ResponseEntity.ok(stationHomeDto);
    }
}
