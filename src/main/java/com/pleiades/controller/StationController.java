package com.pleiades.controller;

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
import java.util.UUID;

@Tag(name = "Station", description = "정거장 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {
    private final AuthService authService;

    private final StationRepository stationRepository;
    private final StationService stationService;
    private final UserRepository userRepository;
    private final UserStationService userStationService;

    @Operation(summary = "정거장 생성", description = "정거장 생성하기")
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

    @Operation(summary = "정거장 삭제", description = "정거장 삭제하기")
    @DeleteMapping("/{station_id}")
    public ResponseEntity<Map<String, String>> deleteStation(HttpServletRequest request, @PathVariable("station_id") String stationPublicId) {
        log.info("deleteStation controller 진입");
        String email = (String) request.getAttribute("email");

        return stationService.deleteStation(email, stationPublicId);
    }

    @Operation(summary = "정거장 배경 설정", description = "정거장 배경 변경하기")
    @PatchMapping("/{station_id}/background")
    public ResponseEntity<Map<String, Object>> updateBackground(@PathVariable("station_id") String stationPublicId, HttpServletRequest request, @RequestBody Map<String, Object> body) {
        log.info("/stations/"+stationPublicId+"/background");
        String email = (String) request.getAttribute("email");
        authService.userInStation(stationPublicId, email);

        Object stationBackground = body.get("stationBackground");
        if (stationBackground == null) { return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(Map.of("message","Station Background required.")); }
        String background = stationBackground.toString();
        log.info("stationBackground: " + background);

        Station station = stationRepository.findByPublicId(UUID.fromString(stationPublicId)).get();
        ValidationStatus setBackground = stationService.setBackground(station, background);

        // background 없음
        if (setBackground == ValidationStatus.NOT_VALID) { throw new CustomException(ErrorCode.IMAGE_NOT_FOUND); }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Background edited"));
    }

    @Operation(summary = "정거장 설정", description = "정거장 설정 변경하기")
    @PatchMapping("/{stationId}/settings")
    public ResponseEntity<Map<String, Object>> stationSetting(@PathVariable("stationId") String stationPublicId, HttpServletRequest request, @Valid @RequestBody StationSettingDto settingDto) {
        log.info("/stations/"+stationPublicId+"/settings");
        String email = (String) request.getAttribute("email");
        authService.userInStation(stationPublicId, email);

        Station station  = stationRepository.findByPublicId(UUID.fromString(stationPublicId)).get();
        stationService.stationSettings(station, settingDto);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Info editted"));
    }

    @Operation(summary = "정거장 코드 재발급", description = "정거장 코드 재발급하기")
    @PatchMapping("/{stationId}/code")
    public ResponseEntity<Map<String, String>> reissueCode(@PathVariable("stationId") String stationPublicId, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        authService.userInStation(stationPublicId, email);

        Station station  = stationRepository.findByPublicId(UUID.fromString(stationPublicId)).get();
        ValidationStatus status = stationService.reissueStationCode(station);

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
        authService.userInStation(stationPublicId, email);

        User user = userRepository.findByEmail(email).get();

        ValidationStatus status = userStationService.setStationFavorite(stationPublicId, user.getId(), true);

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
        authService.userInStation(stationPublicId, email);

        User user = userRepository.findByEmail(email).get();

        ValidationStatus status = userStationService.setStationFavorite(stationPublicId, user.getId(), false);

        if (status == ValidationStatus.NOT_VALID) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete favorite"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Station Favorite Deleted"));
    }
}