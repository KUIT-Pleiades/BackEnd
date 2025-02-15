package com.pleiades.service;

import com.pleiades.dto.station.StationCreateDto;
import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.StationRepository;
import com.pleiades.repository.UserRepository;

import com.pleiades.repository.UserStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final UserStationRepository userStationRepository;

    private final UserStationService userStationService;
    private final UserService userService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // A-Z, 0-9
    private static final int CODE_LENGTH = 6;

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

        Station station = Station.builder()
                .id(stationId)
                .name(requestDto.getName())
                .intro(requestDto.getIntro())
                .numberOfUsers(1)
                .createdAt(LocalDateTime.now())
                .adminUserId(adminUser.getId())
                .reportNoticeTime(requestDto.getReportNoticeTime())
                .backgroundName(requestDto.getBackgroundName())
                .build();

        stationRepository.save(station);
        log.info("새로운 정거장 생성 완료: {}", station.getName());

        userStationService.addUserStation(adminUser, station, true);

        return Map.of("message", "Station created");
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
}
