package com.pleiades.controller;

import com.pleiades.dto.station.StationBgDto;
import com.pleiades.dto.station.StationSettingDto;
import com.pleiades.entity.*;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.station.UserStationService;
import com.pleiades.strings.ValidationStatus;

import com.pleiades.dto.station.StationCreateDto;
import com.pleiades.service.station.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Station", description = "정거장 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;
    private final UserStationService userStationService;

    @Operation(summary = "정거장 생성", description = "정거장 생성하기")
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createStation(HttpServletRequest request, @Valid @RequestBody StationCreateDto requestDto) {
        log.info("createStation controller 진입");
        String email = (String) request.getAttribute("email");

        Map<String, Object> response = stationService.createStation(email, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "정거장 삭제", description = "정거장 삭제하기")
    @DeleteMapping("/{stationId}")
    public ResponseEntity<Map<String, String>> deleteStation(HttpServletRequest request, @PathVariable("stationId") String stationPublicId) {
        log.info("deleteStation controller 진입");
        String email = (String) request.getAttribute("email");

        return stationService.deleteStation(email, stationPublicId);
    }

    @Operation(summary = "정거장 배경 설정", description = "정거장 배경 변경하기")
    @PatchMapping("/{stationId}/background")
    public ResponseEntity<Map<String, Object>> updateBackground(@PathVariable("stationId") String stationPublicId, @RequestBody StationBgDto stationBgDto) {
        log.info("/stations/"+stationPublicId+"/background");
        String email = (String) request.getAttribute("email");

        String stationBackground = stationBgDto.getStationBackground();
        if (stationBackground == null) { return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(Map.of("message","Station Background required.")); }
        log.info("stationBackground: " + stationBackground);

        stationService.setBackground(stationPublicId, stationBackground, email);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Background edited"));
    }

    @Operation(summary = "정거장 설정", description = "정거장 설정 변경하기")
    @PatchMapping("/{stationId}/settings")
    public ResponseEntity<Map<String, Object>> stationSetting(@PathVariable("stationId") String stationPublicId, HttpServletRequest request, @Valid @RequestBody StationSettingDto settingDto) {
        log.info("/stations/"+stationPublicId+"/settings");

        stationService.stationSettings(stationPublicId, settingDto);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Info editted"));
    }

    @Operation(summary = "정거장 코드 재발급", description = "정거장 코드 재발급하기")
    @PatchMapping("/{stationId}/code")
    public ResponseEntity<Map<String, String>> reissueCode(@PathVariable("stationId") String stationPublicId, HttpServletRequest request) {
        ValidationStatus status = stationService.reissueStationCode(stationPublicId);

        if (status == ValidationStatus.NOT_VALID) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to reissue station code"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Successfully reissued station code"));
    }

    @Operation(summary = "정거장 즐겨찾기", description = "정거장 즐겨찾기 설정")
    @PostMapping("/{stationId}/favorite")
    public ResponseEntity<Map<String, Object>> setFavorite(@PathVariable("stationId") String stationPublicId, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");

        ValidationStatus status = userStationService.setStationFavorite(stationPublicId, email, true);

        if (status == ValidationStatus.NOT_VALID) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to set favorite"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Favorite Set"));
    }

    @DeleteMapping("/{stationId}/favorite")
    public ResponseEntity<Map<String, Object>> deleteFavorite(@PathVariable("stationId") String stationPublicId, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");

        ValidationStatus status = userStationService.setStationFavorite(stationPublicId, email, false);

        if (status == ValidationStatus.NOT_VALID) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete favorite"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Favorite Deleted"));
    }
}