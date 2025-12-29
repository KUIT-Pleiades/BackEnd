package com.pleiades.service.station;

import com.pleiades.dto.station.*;
import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.entity.character.TheItem;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.StationRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.UserStationRepository;
import com.pleiades.service.UserService;
import com.pleiades.service.report.TodaysReportService;
import com.pleiades.strings.FriendStatus;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.ValidationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private UserStationRepository userStationRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserService userService;

    @Mock
    private TodaysReportService todaysReportService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserStationService userStationService;

    @Test
    @DisplayName("setUserPosition - 정거장이 존재하지 않을 때 예외 발생")
    void setUserPosition_stationNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        String userId = "user2";
        UserPositionDto requestBody = new UserPositionDto(10f, 20f);
        User user = User.builder().id("user1").email(email).build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStationService.setUserPosition(email, stationPublicId, userId, requestBody))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STATION_NOT_FOUND);
    }

    @Test
    @DisplayName("setUserPosition - target 사용자가 정거장에 없을 때 예외 발생")
    void setUserPosition_targetUserNotInStation_throwsException() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        String userId = "user2";
        UserPositionDto requestBody = new UserPositionDto(10f, 20f);
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(new UserStationId(userId, station.getId()))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStationService.setUserPosition(email, stationPublicId, userId, requestBody))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_IN_STATION);
    }

    @Test
    @DisplayName("setUserPosition - 현재 사용자가 정거장 멤버가 아닐 때 예외 발생")
    void setUserPosition_currentUserNotMember_throwsException() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        String userId = "user2";
        UserPositionDto requestBody = new UserPositionDto(10f, 20f);
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();

        UserStation targetUserStation = UserStation.builder()
                .id(new UserStationId(userId, station.getId()))
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(new UserStationId(userId, station.getId())))
                .thenReturn(Optional.of(targetUserStation));
        when(userStationRepository.findById(new UserStationId(user.getId(), station.getId())))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStationService.setUserPosition(email, stationPublicId, userId, requestBody))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN_MEMBER);
    }

    @Test
    @DisplayName("setUserPosition - 정상 케이스")
    void setUserPosition_validRequest_updatesPosition() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        String userId = "user2";
        UserPositionDto requestBody = new UserPositionDto(10f, 20f);
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();

        UserStationId targetId = new UserStationId(userId, station.getId());
        UserStation targetUserStation = UserStation.builder()
                .id(targetId)
                .positionX(0f)
                .positionY(0f)
                .build();

        UserStationId currentId = new UserStationId(user.getId(), station.getId());
        UserStation currentUserStation = UserStation.builder()
                .id(currentId)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(targetId)).thenReturn(Optional.of(targetUserStation));
        when(userStationRepository.findById(currentId)).thenReturn(Optional.of(currentUserStation));
        when(userStationRepository.save(any(UserStation.class))).thenReturn(targetUserStation);

        // when
        Map<String, String> result = userStationService.setUserPosition(email, stationPublicId, userId, requestBody);

        // then
        assertThat(result).containsEntry("message", "User Position in station editted");
        assertThat(targetUserStation.getPositionX()).isEqualTo(10f);
        assertThat(targetUserStation.getPositionY()).isEqualTo(20f);
        verify(userStationRepository).save(targetUserStation);
    }

    @Test
    @DisplayName("enterStation - 정거장이 존재하지 않을 때 예외 발생")
    void enterStation_stationNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStationService.enterStation(email, stationPublicId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STATION_NOT_FOUND);
    }

    @Test
    @DisplayName("enterStation - 사용자가 정거장 멤버가 아닐 때 예외 발생")
    void enterStation_userNotMember_throwsException() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(new UserStationId(user.getId(), station.getId())))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStationService.enterStation(email, stationPublicId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN_MEMBER);
    }

    @Test
    @DisplayName("enterStation - 정상 케이스")
    void enterStation_validRequest_returnsStationHomeDto() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder()
                .id(1L)
                .adminUserId("user1")
                .name("정거장")
                .intro("소개")
                .numberOfUsers(1)
                .reportNoticeTime(LocalTime.of(9, 0))
                .build();

        TheItem background = TheItem.builder().id(1L).name("background1").build();
        station.setBackground(background);

        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        UserStation userStation = UserStation.builder()
                .id(userStationId)
                .user(user)
                .station(station)
                .todayReport(true)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(userStationId)).thenReturn(Optional.of(userStation));
        when(todaysReportService.searchTodaysReport(email, station.getPublicId().toString()))
                .thenReturn(Report.builder().build());
        when(userStationRepository.findByStationId(station.getId())).thenReturn(List.of(userStation));
        when(friendRepository.isFriend(any(User.class), any(User.class), eq(FriendStatus.ACCEPTED)))
                .thenReturn(false);

        // when
        StationHomeDto result = userStationService.enterStation(email, stationPublicId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStationId()).isEqualTo(station.getPublicId().toString());
        assertThat(result.getName()).isEqualTo("정거장");
    }

    @Test
    @DisplayName("enterStation - 오늘의 리포트가 없을 때 생성")
    void enterStation_noTodaysReport_createsReport() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder()
                .id(1L)
                .adminUserId("user1")
                .name("정거장")
                .numberOfUsers(1)
                .reportNoticeTime(LocalTime.of(9, 0))
                .build();

        TheItem background = TheItem.builder().id(1L).name("background1").build();
        station.setBackground(background);

        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        UserStation userStation = UserStation.builder()
                .id(userStationId)
                .user(user)
                .station(station)
                .todayReport(false)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(userStationId)).thenReturn(Optional.of(userStation));
        when(todaysReportService.searchTodaysReport(email, station.getPublicId().toString())).thenReturn(null);
        when(todaysReportService.createTodaysReport(email, station.getPublicId().toString()))
                .thenReturn(Report.builder().build());
        when(userStationRepository.findByStationId(station.getId())).thenReturn(List.of(userStation));
        when(friendRepository.isFriend(any(User.class), any(User.class), eq(FriendStatus.ACCEPTED)))
                .thenReturn(false);

        // when
        StationHomeDto result = userStationService.enterStation(email, stationPublicId);

        // then
        assertThat(result).isNotNull();
        verify(todaysReportService).createTodaysReport(email, station.getPublicId().toString());
    }

    @Test
    @DisplayName("addMemberToStation - 정거장이 존재하지 않을 때 예외 발생")
    void addMemberToStation_stationNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String stationCode = "ABC123";
        User user = User.builder().id("user1").email(email).build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByCode(stationCode)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStationService.addMemberToStation(email, stationCode))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STATION_NOT_FOUND);
    }

    @Test
    @DisplayName("addMemberToStation - 이미 정거장 멤버일 때 예외 발생")
    void addMemberToStation_alreadyMember_throwsException() {
        // given
        String email = "test@example.com";
        String stationCode = "ABC123";
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByCode(stationCode)).thenReturn(Optional.of(station));
        when(userStationRepository.existsById(new UserStationId(user.getId(), station.getId()))).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userStationService.addMemberToStation(email, stationCode))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_ALREADY_IN_STATION);
    }

    @Test
    @DisplayName("addMemberToStation - 정상 케이스")
    void addMemberToStation_validRequest_addsMember() {
        // given
        String email = "test@example.com";
        String stationCode = "ABC123";
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder()
                .id(1L)
                .numberOfUsers(1)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByCode(stationCode)).thenReturn(Optional.of(station));
        when(userStationRepository.existsById(new UserStationId(user.getId(), station.getId()))).thenReturn(false);
        when(userStationRepository.countByStationId(station.getId())).thenReturn(1);
        doNothing().when(userStationService).addUserStation(user, station, false);
        when(stationRepository.save(any(Station.class))).thenReturn(station);
        when(todaysReportService.createTodaysReport(email, station.getPublicId().toString()))
                .thenReturn(Report.builder().build());

        // when
        Map<String, String> result = userStationService.addMemberToStation(email, stationCode);

        // then
        assertThat(result).containsKey("stationId");
        assertThat(station.getNumberOfUsers()).isEqualTo(2);
        verify(userStationService).addUserStation(user, station, false);
        verify(stationRepository).save(station);
    }

    @Test
    @DisplayName("addUserStation - 정거장이 가득 찰 때 예외 발생")
    void addUserStation_stationFull_throwsException() {
        // given
        User user = User.builder().id("user1").build();
        Station station = Station.builder().id(1L).build();

        when(userStationRepository.countByStationId(station.getId())).thenReturn(6);

        // when & then
        assertThatThrownBy(() -> userStationService.addUserStation(user, station, false))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STATION_FULL);
    }

    @Test
    @DisplayName("addUserStation - 정상 케이스")
    void addUserStation_validRequest_createsUserStation() {
        // given
        User user = User.builder().id("user1").userName("사용자").build();
        Station station = Station.builder().id(1L).build();

        when(userStationRepository.countByStationId(station.getId())).thenReturn(0);
        when(userStationRepository.save(any(UserStation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userStationService.addUserStation(user, station, true);

        // then
        verify(userStationRepository).save(any(UserStation.class));
    }

    @Test
    @DisplayName("getStationList - 정상 케이스")
    void getStationList_validRequest_returnsStationList() {
        // given
        String email = "test@example.com";
        User currentUser = User.builder().id("user1").email(email).build();
        Station station = Station.builder()
                .id(1L)
                .name("정거장")
                .numberOfUsers(1)
                .code("ABC123")
                .build();

        TheItem background = TheItem.builder().id(1L).name("background1").build();
        station.setBackground(background);

        UserStation userStation = UserStation.builder()
                .id(new UserStationId(currentUser.getId(), station.getId()))
                .user(currentUser)
                .station(station)
                .favorite(false)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(currentUser);
        when(userStationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()))
                .thenReturn(List.of(userStation));

        // when
        StationListDto result = userStationService.getStationList(email);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStations()).hasSize(1);
        assertThat(result.getStations().get(0).getName()).isEqualTo("정거장");
    }

    @Test
    @DisplayName("setStationFavorite - 사용자가 존재하지 않을 때 예외 발생")
    void setStationFavorite_userNotFound_throwsException() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStationService.setStationFavorite(stationPublicId, email, true))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("setStationFavorite - UserStation이 없을 때 NOT_VALID 반환")
    void setStationFavorite_userStationNotFound_returnsNotValid() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userStationRepository.findByStationPublicIdAndUserId(UUID.fromString(stationPublicId), user.getId()))
                .thenReturn(Optional.empty());

        // when
        ValidationStatus result = userStationService.setStationFavorite(stationPublicId, email, true);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
    }

    @Test
    @DisplayName("setStationFavorite - 정상 케이스")
    void setStationFavorite_validRequest_returnsValid() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();

        UserStation userStation = UserStation.builder()
                .id(new UserStationId(user.getId(), station.getId()))
                .user(user)
                .station(station)
                .favorite(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userStationRepository.findByStationPublicIdAndUserId(UUID.fromString(stationPublicId), user.getId()))
                .thenReturn(Optional.of(userStation));
        when(userStationRepository.save(any(UserStation.class))).thenReturn(userStation);

        // when
        ValidationStatus result = userStationService.setStationFavorite(stationPublicId, email, true);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
        assertThat(userStation.isFavorite()).isTrue();
        verify(userStationRepository).save(userStation);
    }
}

