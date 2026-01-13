package com.pleiades.service.station;

import com.pleiades.dto.station.*;
import com.pleiades.entity.Report;
import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;

import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.StationRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.UserStationRepository;
import com.pleiades.service.UserService;
import com.pleiades.service.report.TodaysReportService;
import com.pleiades.strings.FriendStatus;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStationService {

    private final StationRepository stationRepository;
    private final UserStationRepository userStationRepository;
    private final FriendRepository friendRepository;

    private final UserService userService;
    private final TodaysReportService todaysReportService;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, String> setUserPosition(String email, String stationPublicId, String userId, UserPositionDto requestBody) {
        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 정거장 존재 여부 확인 (404)
//        stationRepository.findByPublicId(UUID.fromString(stationPublicId)).orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND));
        Station station = stationRepository.findByPublicId(UUID.fromString(stationPublicId)).orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND));

        // target 사용자가 정거장에 있는지 확인 (404)
        UserStation targetUserStation = userStationRepository.findById(new UserStationId(userId, station.getId()))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_IN_STATION));

        // 사용자가 정거장 멤버인지 확인 (403)
        userStationRepository.findById(new UserStationId(user.getId(), station.getId()))
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_MEMBER));

        targetUserStation.setPositionX(requestBody.getPositionX());
        targetUserStation.setPositionY(requestBody.getPositionY());

        userStationRepository.save(targetUserStation);
        log.info("사용자({})가 사용자({})의 위치를 변경: X={}, Y={}", user.getId(), userId, requestBody.getPositionX(), requestBody.getPositionY());

        return Map.of("message", "User Position in station editted");
    }

    // 정거장 홈 _ 입장
    @Transactional
    public StationHomeDto enterStation(String email, String stationPublicId) {
        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 정거장 존재 여부 확인 (404)
        Station station = stationRepository.findByPublicId(UUID.fromString(stationPublicId))
                .orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND));

        // 사용자가 정거장 멤버인지 확인 (403)
        UserStation userStation = userStationRepository.findById(new UserStationId(user.getId(), station.getId()))
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN_MEMBER));

        // 투데이 리포트 '생성' 여부 검증 - 안 됐으면 생성
        Report todaysReport = todaysReportService.searchTodaysReport(email, station.getPublicId().toString());
        if (todaysReport == null) {
            todaysReportService.createTodaysReport(email, station.getPublicId().toString());
        }

        // response DTO 생성
        return buildStationHomeDto(station, userStation.isTodayReport(), user);
    }

    // 정거장 첫 입장 _ 멤버 추가
    @Transactional
    public Map<String, String> addMemberToStation(String email, String stationCode) {
        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 정거장 존재 여부 확인 (404)
        Station station = stationRepository.findByCode(stationCode)
                .orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND));

        // 이미 해당 station member 인지 확인 (409)
        boolean isUserAlreadyInStation = userStationRepository.existsById(new UserStationId(user.getId(), station.getId()));
        if (isUserAlreadyInStation) {
            throw new CustomException(ErrorCode.USER_ALREADY_IN_STATION);
        }

        // 새 UserStation 추가 (멤버 등록)
        addUserStation(user, station, false);

        // 정거장 인원수 업데이트
        station.setNumberOfUsers(station.getNumberOfUsers() + 1);
        stationRepository.save(station);

        Report report = todaysReportService.createTodaysReport(email, station.getPublicId().toString());
        log.info("새로운 리포트 생성 완료: {}", report.getQuestion());

        return Map.of("stationId", station.getPublicId().toString());
    }

    // response DTO 형성 method
    @Transactional
    public StationHomeDto buildStationHomeDto(Station station, boolean reportWritten, User currentUser) {
        List<UserStation> userStations = userStationRepository.findByStationId(station.getId());

        List<StationMemberDto> members = userStations.stream()
                .map(userStation -> {
                    User member = userStation.getUser();
                    return new StationMemberDto(
                            member.getId(),
                            member.getUserName(),
                            member.getProfileUrl(),
                            member.getCharacterUrl(),
                            userStation.getPositionX(),
                            userStation.getPositionY(),
                            userStation.isTodayReport(),
                            friendRepository.isFriend(currentUser, member, FriendStatus.ACCEPTED)
                    );
                })
                .collect(Collectors.toList());

        return new StationHomeDto(
                station.getPublicId().toString(),
                station.getCode(),
                station.getAdminUserId(),
                station.getName(),
                station.getIntro(),
                station.getNumberOfUsers(),
                station.getBackground().getName(),
                station.getReportNoticeTime(),
                reportWritten,
                members
        );
    }

    // 사용자 _ 우주 정거장 관계 테이블 객체 추가
    @Transactional
    public void addUserStation(User user, Station station, boolean isAdmin) {

        int currentMembers = userStationRepository.countByStationId(station.getId());

        if (currentMembers >= 6) {
            throw new CustomException(ErrorCode.STATION_FULL);
        }
        // 입장 순서에 따른 X, Y 좌표 설정
        float[] xPositions = {10f, 35f, 60f};
        float[] yPositions = {40f, 60f};

        float positionX = xPositions[currentMembers % 3];
        float positionY = yPositions[currentMembers / 3];

        UserStation userStation = UserStation.builder()
                .id(new UserStationId(user.getId(), station.getId()))
                .user(user)
                .station(station)
                .isAdmin(isAdmin)
                .createdAt(LocalDateTimeUtil.now())
                .todayReport(false)
                .positionX(positionX) // 기본 위치값 설정
                .positionY(positionY)
                .favorite(false)
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
                            station.getPublicId().toString(),
                            station.getCode(),
                            station.getName(),
                            station.getNumberOfUsers(),
                            station.getBackground().getName(),
                            station.getCreatedAt(),
                            station.getRecentActivity(),
                            userStation.isFavorite()
                    );
                })
                .collect(Collectors.toList());

        return new StationListDto(stationDtos);
    }

    @Transactional
    public ValidationStatus setStationFavorite(String stationPublicId, String email, boolean isFavorite) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Optional<UserStation> userStation = userStationRepository.findByStationPublicIdAndUserId(UUID.fromString(stationPublicId), user.getId());

        if (userStation.isEmpty()) {
            return ValidationStatus.NOT_VALID;
        }

        userStation.get().setFavorite(isFavorite);
        userStationRepository.save(userStation.get());
        return ValidationStatus.VALID;
    }

    @Transactional
    public void leaveStation(UserStation userStation) {
        Station station = userStation.getStation();

        // 관계 제거
        userStationRepository.delete(userStation);

        // station 사용자 수 감소
        station.decreaseNumberOfUsers();
    }

    @Transactional
    public void leaveAllStations(User user) {
        List<UserStation> memberships =
                userStationRepository.findByUser(user);

        for (UserStation us : memberships) {
            if (!us.isAdmin()) {
                leaveStation(us);
            }
        }
    }
}
