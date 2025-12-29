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
import com.pleiades.repository.store.OwnershipRepository;
import com.pleiades.service.UserService;
import com.pleiades.service.report.TodaysReportService;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.ValidationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private UserStationRepository userStationRepository;

    @Mock
    private OwnershipRepository ownershipRepository;

    @Mock
    private UserStationService userStationService;

    @Mock
    private UserService userService;

    @Mock
    private TodaysReportService todaysReportService;

    @Mock
    private TheItemRepository theItemRepository;

    @InjectMocks
    private StationService stationService;

    @Test
    @DisplayName("deleteStation - 정거장이 존재하지 않을 때 예외 발생")
    void deleteStation_stationNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.deleteStation(email, stationPublicId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STATION_NOT_FOUND);
    }

    @Test
    @DisplayName("deleteStation - 방장이 정거장을 삭제할 때")
    void deleteStation_adminDeletesStation_returnsDeleted() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder()
                .id(1L)
                .adminUserId("user1")
                .name("정거장")
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        doNothing().when(userStationRepository).deleteAllByStationId(station.getId());
        doNothing().when(stationRepository).delete(station);

        // when
        ResponseEntity<Map<String, String>> response = stationService.deleteStation(email, stationPublicId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Station deleted");
        verify(userStationRepository).deleteAllByStationId(station.getId());
        verify(stationRepository).delete(station);
    }

    @Test
    @DisplayName("deleteStation - 일반 사용자가 정거장을 나갈 때")
    void deleteStation_userExitsStation_returnsExitted() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user2").email(email).build();
        Station station = Station.builder()
                .id(1L)
                .adminUserId("user1")
                .name("정거장")
                .build();

        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        UserStation userStation = UserStation.builder()
                .id(userStationId)
                .user(user)
                .station(station)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(userStationId)).thenReturn(Optional.of(userStation));
        doNothing().when(userStationRepository).delete(userStation);

        // when
        ResponseEntity<Map<String, String>> response = stationService.deleteStation(email, stationPublicId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "User exitted the station");
        verify(userStationRepository).delete(userStation);
        verify(stationRepository, never()).delete(any(Station.class));
    }

    @Test
    @DisplayName("deleteStation - 일반 사용자가 정거장에 없을 때 예외 발생")
    void deleteStation_userNotInStation_throwsException() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user2").email(email).build();
        Station station = Station.builder()
                .id(1L)
                .adminUserId("user1")
                .build();

        UserStationId userStationId = new UserStationId(user.getId(), station.getId());

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(userStationId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.deleteStation(email, stationPublicId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN_MEMBER);
    }

    @Test
    @DisplayName("removeAllUsersFromStation - 정상 케이스")
    void removeAllUsersFromStation_validStation_deletesAllUsers() {
        // given
        Station station = Station.builder().id(1L).build();

        doNothing().when(userStationRepository).deleteAllByStationId(station.getId());

        // when
        stationService.removeAllUsersFromStation(station);

        // then
        verify(userStationRepository).deleteAllByStationId(station.getId());
    }

    @Test
    @DisplayName("createStation - 정상 케이스")
    void createStation_validRequest_createsStation() {
        // given
        String email = "test@example.com";
        User adminUser = User.builder().id("user1").email(email).build();

        StationCreateDto requestDto = new StationCreateDto();
        requestDto.setName("새 정거장");
        requestDto.setIntro("소개");
        requestDto.setStationBackground("background1");
        requestDto.setReportNoticeTime(LocalTime.of(9, 0));

        TheItem background = TheItem.builder()
                .id(1L)
                .name("background1")
                .type(ItemType.STATION_BG)
                .build();

        Station station = Station.builder()
                .id(1L)
                .name("새 정거장")
                .adminUserId("user1")
                .code("ABC123")
                .build();

        Report report = Report.builder().id(1L).build();

        when(userService.getUserByEmail(email)).thenReturn(adminUser);
        when(stationRepository.existsByCode(anyString())).thenReturn(false);
        when(theItemRepository.findByTypeAndName(ItemType.STATION_BG, "background1"))
                .thenReturn(Optional.of(background));
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> {
            Station s = invocation.getArgument(0);
            s.setPublicId(UUID.randomUUID());
            return s;
        });
        doNothing().when(userStationService).addUserStation(adminUser, any(Station.class), true);
        when(todaysReportService.createTodaysReport(email, anyString())).thenReturn(report);

        // when
        Map<String, Object> result = stationService.createStation(email, requestDto);

        // then
        assertThat(result).containsKey("stationId");
        verify(stationRepository).save(any(Station.class));
        verify(userStationService).addUserStation(adminUser, any(Station.class), true);
        verify(todaysReportService).createTodaysReport(email, anyString());
    }

    @Test
    @DisplayName("createStation - 배경이 없을 때 기본 배경 사용")
    void createStation_backgroundNotFound_usesDefaultBackground() {
        // given
        String email = "test@example.com";
        User adminUser = User.builder().id("user1").email(email).build();

        StationCreateDto requestDto = new StationCreateDto();
        requestDto.setName("새 정거장");
        requestDto.setStationBackground("nonexistent");

        TheItem defaultBackground = TheItem.builder()
                .id(1L)
                .name("station_dim_01.png")
                .type(ItemType.STATION_BG)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(adminUser);
        when(stationRepository.existsByCode(anyString())).thenReturn(false);
        when(theItemRepository.findByTypeAndName(ItemType.STATION_BG, "nonexistent"))
                .thenReturn(Optional.empty());
        when(theItemRepository.findByTypeAndName(ItemType.STATION_BG, "station_dim_01.png"))
                .thenReturn(Optional.of(defaultBackground));
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> {
            Station s = invocation.getArgument(0);
            s.setPublicId(UUID.randomUUID());
            return s;
        });
        doNothing().when(userStationService).addUserStation(any(User.class), any(Station.class), anyBoolean());
        when(todaysReportService.createTodaysReport(anyString(), anyString()))
                .thenReturn(Report.builder().build());

        // when
        Map<String, Object> result = stationService.createStation(email, requestDto);

        // then
        assertThat(result).containsKey("stationId");
        verify(theItemRepository).findByTypeAndName(ItemType.STATION_BG, "station_dim_01.png");
    }

    @Test
    @DisplayName("setBackground - 정거장이 존재하지 않을 때 예외 발생")
    void setBackground_stationNotFound_throwsException() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        String backgroundName = "background1";
        String email = "test@example.com";

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.setBackground(stationPublicId, backgroundName, email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STATION_NOT_FOUND);
    }

    @Test
    @DisplayName("setBackground - 배경이 존재하지 않을 때 예외 발생")
    void setBackground_backgroundNotFound_throwsException() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        String backgroundName = "background1";
        String email = "test@example.com";
        Station station = Station.builder().id(1L).build();
        User user = User.builder().id("user1").email(email).build();

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(theItemRepository.findByTypeAndName(ItemType.STATION_BG, backgroundName))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.setBackground(stationPublicId, backgroundName, email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.IMAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("setBackground - 기본 배경 설정")
    void setBackground_basicBackground_setsBackground() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        String backgroundName = "background1";
        String email = "test@example.com";
        Station station = Station.builder().id(1L).build();
        User user = User.builder().id("user1").email(email).build();

        TheItem background = TheItem.builder()
                .id(1L)
                .name(backgroundName)
                .type(ItemType.STATION_BG)
                .isBasic(true)
                .build();

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(theItemRepository.findByTypeAndName(ItemType.STATION_BG, backgroundName))
                .thenReturn(Optional.of(background));
        when(stationRepository.save(any(Station.class))).thenReturn(station);

        // when
        stationService.setBackground(stationPublicId, backgroundName, email);

        // then
        assertThat(station.getBackground()).isEqualTo(background);
        assertThat(station.getBackgroundOwner()).isNull();
        verify(stationRepository).save(station);
    }

    @Test
    @DisplayName("setBackground - 소유권이 있는 배경 설정")
    void setBackground_ownedBackground_setsBackground() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        String backgroundName = "background1";
        String email = "test@example.com";
        Station station = Station.builder().id(1L).build();
        User user = User.builder().id("user1").email(email).build();

        TheItem background = TheItem.builder()
                .id(1L)
                .name(backgroundName)
                .type(ItemType.STATION_BG)
                .isBasic(false)
                .build();

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(theItemRepository.findByTypeAndName(ItemType.STATION_BG, backgroundName))
                .thenReturn(Optional.of(background));
        when(ownershipRepository.existsByUserIdAndItemId(user.getId(), background.getId())).thenReturn(true);
        when(stationRepository.save(any(Station.class))).thenReturn(station);

        // when
        stationService.setBackground(stationPublicId, backgroundName, email);

        // then
        assertThat(station.getBackground()).isEqualTo(background);
        assertThat(station.getBackgroundOwner()).isEqualTo(user);
        verify(stationRepository).save(station);
    }

    @Test
    @DisplayName("setBackground - 소유권이 없는 배경일 때 예외 발생")
    void setBackground_noOwnership_throwsException() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        String backgroundName = "background1";
        String email = "test@example.com";
        Station station = Station.builder().id(1L).build();
        User user = User.builder().id("user1").email(email).build();

        TheItem background = TheItem.builder()
                .id(1L)
                .name(backgroundName)
                .type(ItemType.STATION_BG)
                .isBasic(false)
                .build();

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(theItemRepository.findByTypeAndName(ItemType.STATION_BG, backgroundName))
                .thenReturn(Optional.of(background));
        when(ownershipRepository.existsByUserIdAndItemId(user.getId(), background.getId())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> stationService.setBackground(stationPublicId, backgroundName, email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NO_OWNERSHIP);
    }

    @Test
    @DisplayName("stationSettings - 정거장이 존재하지 않을 때 예외 발생")
    void stationSettings_stationNotFound_throwsException() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        StationSettingDto settingDto = new StationSettingDto();
        settingDto.setName("새 이름");

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.stationSettings(stationPublicId, settingDto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STATION_NOT_FOUND);
    }

    @Test
    @DisplayName("stationSettings - 정상 케이스")
    void stationSettings_validRequest_updatesStation() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        Station station = Station.builder()
                .id(1L)
                .name("기존 이름")
                .intro("기존 소개")
                .build();

        StationSettingDto settingDto = new StationSettingDto();
        settingDto.setName("새 이름");
        settingDto.setIntro("새 소개");
        settingDto.setReportNoticeTime(LocalTime.of(10, 0));

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(stationRepository.save(any(Station.class))).thenReturn(station);

        // when
        stationService.stationSettings(stationPublicId, settingDto);

        // then
        assertThat(station.getName()).isEqualTo("새 이름");
        assertThat(station.getIntro()).isEqualTo("새 소개");
        assertThat(station.getReportNoticeTime()).isEqualTo(LocalTime.of(10, 0));
        verify(stationRepository).save(station);
    }

    @Test
    @DisplayName("reissueStationCode - 정거장이 존재하지 않을 때 예외 발생")
    void reissueStationCode_stationNotFound_throwsException() {
        // given
        String stationPublicId = UUID.randomUUID().toString();

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.reissueStationCode(stationPublicId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STATION_NOT_FOUND);
    }

    @Test
    @DisplayName("reissueStationCode - 정상 케이스")
    void reissueStationCode_validRequest_returnsValid() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        Station station = Station.builder()
                .id(1L)
                .code("OLD123")
                .build();

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(stationRepository.count()).thenReturn(10L);
        when(stationRepository.existsByCode(anyString())).thenReturn(false);
        when(stationRepository.save(any(Station.class))).thenReturn(station);

        // when
        ValidationStatus result = stationService.reissueStationCode(stationPublicId);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
        assertThat(station.getCode()).isNotEqualTo("OLD123");
        verify(stationRepository).save(station);
    }

    @Test
    @DisplayName("reissueStationCode - 코드 생성 실패 시 NOT_VALID 반환")
    void reissueStationCode_codeGenerationFailed_returnsNotValid() {
        // given
        String stationPublicId = UUID.randomUUID().toString();
        Station station = Station.builder()
                .id(1L)
                .code("OLD123")
                .build();

        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(stationRepository.count()).thenReturn(1L);
        when(stationRepository.existsByCode(anyString())).thenReturn(true); // 모든 코드가 중복

        // when
        ValidationStatus result = stationService.reissueStationCode(stationPublicId);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
        verify(stationRepository, never()).save(any(Station.class));
    }
}

