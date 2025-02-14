package com.pleiades.service;

import com.pleiades.dto.station.StationDto;
import com.pleiades.dto.station.StationHomeDto;
import com.pleiades.dto.station.StationListDto;
import com.pleiades.dto.station.StationMemberDto;
import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;

import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.StationRepository;
import com.pleiades.repository.UserStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStationService {

    private final StationRepository stationRepository;
    private final UserStationRepository userStationRepository;

    private final UserService userService;

    // 정거장 홈 _ 입장
    @Transactional
    public StationHomeDto enterStation(String email, String stationId) {
        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 정거장 존재 여부 확인 (404)
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND));

        // 사용자가 정거장 멤버인지 확인 (403)
        UserStation userStation = userStationRepository.findById(new UserStationId(user.getId(), stationId))
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_MEMBER));

        // response DTO 생성
        return buildStationHomeDto(station, userStation.isTodayReport());
    }

    // 정거장 첫 입장 _ 멤버 추가
    @Transactional
    public StationHomeDto addMemberToStation(String email, String stationId) {
        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 정거장 존재 여부 확인 (404)
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND));

        // 이미 해당 station member 인지 확인 (409)
        boolean isUserAlreadyInStation = userStationRepository.existsById(new UserStationId(user.getId(), stationId));
        if (isUserAlreadyInStation) {
            throw new CustomException(ErrorCode.USER_ALREADY_IN_STATION);
        }

        // 새 UserStation 추가 (멤버 등록)
        addUserStation(user, station, false);

        // 정거장 인원수 업데이트
        station.setNumberOfUsers(station.getNumberOfUsers() + 1);
        stationRepository.save(station);

        // response DTO 생성
        return buildStationHomeDto(station, false);
    }

    // response DTO 형성 method
    @Transactional
    public StationHomeDto buildStationHomeDto(Station station, boolean reportWritten) {
        List<UserStation> userStations = userStationRepository.findByStationId(station.getId());

        List<StationMemberDto> members = userStations.stream()
                .map(userStation -> {
                    User member = userStation.getUser();
                    return new StationMemberDto(
                            member.getId(),
                            member.getUserName(),
                            member.getProfileUrl(),
                            userStation.getPositionX(),
                            userStation.getPositionY(),
                            userStation.isTodayReport()
                    );
                })
                .collect(Collectors.toList());

        return new StationHomeDto(
                station.getId(),
                station.getAdminUserId(),
                station.getName(),
                station.getIntro(),
                station.getNumberOfUsers(),
                station.getBackgroundName(),
                station.getReportNoticeTime(),
                reportWritten,
                members
        );
    }

    // 사용자 _ 우주 정거장 관계 테이블 객체 추가
    @Transactional
    public void addUserStation(User user, Station station, boolean isAdmin) {

        UserStation userStation = UserStation.builder()
                .id(new UserStationId(user.getId(), station.getId()))
                .user(user)
                .station(station)
                .isAdmin(isAdmin)
                .createdAt(LocalDateTime.now())
                .todayReport(false)
                // TODO: default Position X,Y 변경
                .positionX(50f) // 기본 위치값 설정
                .positionY(70f)
                .build();

        userStationRepository.save(userStation);
        log.info("사용자 정보 UserStation 에 저장 완료: {}", user.getUserName());
    }

    @Transactional
    public StationListDto getStationList(String email) {
        User currentUser = userService.getUserByEmail(email);

        List<UserStation> userStations = userStationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        List<StationDto> stationDtos = userStations.stream()
                .map(userStation -> {
                    Station station = userStation.getStation();
                    return new StationDto(
                            station.getId(),
                            station.getName(),
                            station.getNumberOfUsers(),
                            station.getBackgroundName()
                    );
                })
                .collect(Collectors.toList());

        return new StationListDto(stationDtos);
    }
}
