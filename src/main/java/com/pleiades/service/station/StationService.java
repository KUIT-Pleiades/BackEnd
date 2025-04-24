package com.pleiades.service.station;

import com.pleiades.dto.station.StationCreateDto;
import com.pleiades.dto.station.StationSettingDto;
import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.StationBackgroundRepository;
import com.pleiades.repository.StationRepository;

import com.pleiades.repository.UserStationRepository;
import com.pleiades.service.UserService;
import com.pleiades.service.report.TodaysReportService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final UserStationRepository userStationRepository;

    private final UserStationService userStationService;
    private final UserService userService;
    private final TodaysReportService todaysReportService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // A-Z, 0-9
    private static final int CODE_LENGTH = 6;
    private final StationBackgroundRepository stationBackgroundRepository;

    @Transactional
    public ResponseEntity<Map<String, String>> deleteStation(String email, String stationId){
        Map<String, String> response = new HashMap<>();

        User user = userService.getUserByEmail(email);
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND));

        // 방장인지 확인
        if (station.getAdminUserId().equals(user.getId())) {
            // 방장: 정거장 폭파 (모든 관계 삭제)
            log.info("방장({}) -> 정거장 삭제 {}", user.getEmail(), station.getName());

            removeAllUsersFromStation(station); // 모든 사용자 삭제
            stationRepository.delete(station); // 정거장 삭제

            response.put("message", "Station deleted");
        } else {
            // 방장 X: 정거장 나가기 (사용자_정거장 관계만 삭제)
            log.info("일반 사용자({}) -> 정거장 나가기: {}", user.getEmail(), station.getName());

            UserStationId userStationId = new UserStationId(user.getId(), stationId);
            UserStation userStation = userStationRepository.findById(userStationId)
                    .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_MEMBER));

            userStationRepository.delete(userStation);
            response.put("message", "User exitted the station");
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional
    public void removeAllUsersFromStation(Station station) {
        userStationRepository.deleteAllByStationId(station.getId());
    }

    @Transactional
    public Map<String, Object> createStation(String email, StationCreateDto requestDto) {

        User adminUser = userService.getUserByEmail(email);
        String stationId = generateUniqueStationCode();
        StationBackground stationBackground = stationBackgroundRepository.findByName(requestDto.getStationBackground()).orElse(null);
        if (stationBackground == null) {
            log.info("station background not found");
            stationBackground = stationBackgroundRepository.findByName("station_dim_01").orElseThrow();
        }

        Station station = Station.builder()
                .id(stationId)
                .name(requestDto.getName())
                .intro(requestDto.getIntro())
                .numberOfUsers(1)
                .createdAt(LocalDateTimeUtil.now())
                .adminUserId(adminUser.getId())
                .reportNoticeTime(requestDto.getReportNoticeTime())
                .background(stationBackground)
                .build();

        stationRepository.save(station);
        log.info("새로운 정거장 생성 완료: {}", station.getName());

        userStationService.addUserStation(adminUser, station, true);
        Report report = todaysReportService.createTodaysReport(adminUser,station);
        log.info("새로운 리포트 생성 완료: {}", report.getQuestion());

        return Map.of("stationId", stationId);
    }


    private String generateUniqueStationCode() {
        String code;
        do {
            code = generateStationCode();
        } while (stationRepository.existsById(code)); // 중복 체크

        return code;
    }

    // A-Z, 0-9 6자리 랜덤 코드 생성
    private String generateStationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        Random random = ThreadLocalRandom.current();

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return code.toString();
    }

    public ValidationStatus setBackground(Station station, String backgroundName) {
        log.info("setBackground");

        Optional<StationBackground> background = stationBackgroundRepository.findByName(backgroundName);
        if (background.isEmpty()) {
            log.info("background not found");
            return ValidationStatus.NOT_VALID;
        }

        background.ifPresent(station::setBackground);

        stationRepository.save(station);

        return ValidationStatus.VALID;
    }

    public void stationSettings(Station station, StationSettingDto settingDto) {
        log.info("stationSettings");

        station.setName(settingDto.getName());
        station.setIntro(settingDto.getIntro());
        station.setReportNoticeTime(settingDto.getReportNoticeTime());

        stationRepository.save(station);
    }
}
