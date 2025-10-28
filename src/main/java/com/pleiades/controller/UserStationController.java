package com.pleiades.controller;

import com.pleiades.dto.station.StationHomeDto;
import com.pleiades.dto.station.StationListDto;
import com.pleiades.dto.station.UserPositionDto;
import com.pleiades.service.station.UserStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "UserStation", description = "정거장 입장 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class UserStationController {

    private final UserStationService userStationService;

    @Operation(summary = "사용자 위치 설정", description = "정거장 내 사용자 위치 설정하기")
    @PatchMapping("/{stationId}/users/{userId}/position")
    public ResponseEntity<Map<String, String>> setUserPosition(HttpServletRequest request, @Valid @RequestBody UserPositionDto requestBody, @PathVariable("stationId") String stationPublicId, @PathVariable("userId") String userId){
        log.info("setUserPosition controller 진입");

        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        log.info("사용자 email = {}", email);

        Map<String, String> response = userStationService.setUserPosition(email, stationPublicId, userId, requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "정거장 목록", description = "사용자가 속해있는 정거장들의 목록 불러오기")
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

    @Operation(summary = "정거장 가입", description = "코드에 해당하는 정거장 가입하기")
    @PatchMapping("/{stationCode}")
    public ResponseEntity<Map<String, String>> addUserStation(@PathVariable("stationCode") String stationCode, HttpServletRequest request) {
        log.info("멤버 추가: add UserStation Controller 진입");
        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 401
        log.info("사용자 email = {}", email);

        Map<String, String> response = userStationService.addMemberToStation(email, stationCode);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "정거장 입장", description = "id에 해당하는 정거장 입장하기")
    @GetMapping("/{stationCode}")
    public ResponseEntity<StationHomeDto> enterStation(@PathVariable("stationCode") String stationCode, HttpServletRequest request) {
        log.info("정거장 입장 Controller 진입");
        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } // 401
        log.info("사용자 email = {}", email);

        StationHomeDto stationHomeDto = userStationService.enterStation(email, stationCode);

        if(!stationHomeDto.isReportWritten()){ // 202
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(stationHomeDto);
        }
        return ResponseEntity.ok(stationHomeDto);
    }
}
