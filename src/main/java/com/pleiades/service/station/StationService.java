package com.pleiades.service.station;

import com.pleiades.dto.station.StationCreateDto;
import com.pleiades.dto.station.StationSettingDto;
import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.entity.character.TheItem;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.StationRepository;

import com.pleiades.repository.UserStationRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.service.UserService;
import com.pleiades.service.report.TodaysReportService;
import com.pleiades.strings.ItemType;
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
    private final TheItemRepository theItemRepository;

    @Transactional
    public ResponseEntity<Map<String, String>> deleteStation(String email, String stationPublicId){
        Map<String, String> response = new HashMap<>();

        User user = userService.getUserByEmail(email);
        Station station = stationRepository.findByPublicId(UUID.fromString(stationPublicId))        // findById -> findByPublicId
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

            UserStationId userStationId = new UserStationId(user.getId(), station.getId());
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
//        String stationId = generateUniqueStationCode();
        String stationCode = generateUniqueStationCode();
        TheItem stationBackground = theItemRepository.findByTypeAndName(ItemType.STATION_BG, requestDto.getStationBackground()).orElse(null);
        if (stationBackground == null) {
            log.info("station background not found");
            stationBackground = theItemRepository.findByTypeAndName(ItemType.STATION_BG, "station_dim_01.png").orElseThrow();
        }

        Station station = Station.builder()
//                .id(stationId)
                .name(requestDto.getName())
                .intro(requestDto.getIntro())
                .numberOfUsers(1)
                .createdAt(LocalDateTimeUtil.now())
                .adminUserId(adminUser.getId())
                .reportNoticeTime(requestDto.getReportNoticeTime())
                .background(stationBackground)
                .recentActivity(LocalDateTimeUtil.now())
//                .code(stationId)        // 초기 정거장 코드는 아이디와 동일
                .code(stationCode)
                .build();

        stationRepository.save(station);
        log.info("새로운 정거장 생성 완료: {}", station.getName());

        userStationService.addUserStation(adminUser, station, true);
        Report report = todaysReportService.createTodaysReport(email, station.getPublicId().toString());
        log.info("새로운 리포트 생성 완료: {}", report.getQuestion());

//        return Map.of("stationId", stationId);
        return Map.of("stationId", station.getPublicId());
    }


    private String generateUniqueStationCode() {
        String code;
        do {
            code = generateStationCode();
        } while (stationRepository.existsByCode(code)); // 중복 체크

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

        Optional<TheItem> background = theItemRepository.findByTypeAndName(ItemType.STATION_BG, backgroundName);
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

    // 정거장 코드 재발급
    public ValidationStatus reissueStationCode(Station station) {
        log.info("reissueStationCode");

        String code = null;
        long count = 0;
        long stationCount = stationRepository.count();
        do {
            if (count > stationCount) { code = null; break; }
            code = generateStationCode();
            count++;
        } while (stationRepository.existsByCode(code)); // 중복 체크

        if (code == null) { return ValidationStatus.NOT_VALID; }

        station.setCode(code);
        stationRepository.save(station);

        return ValidationStatus.VALID;
    }
}
