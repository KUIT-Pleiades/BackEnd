package com.pleiades.service.report;

import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.service.station.StationQuestionService;
import com.pleiades.strings.ValidationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodaysReportServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private UserStationRepository userStationRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private StationReportRepository stationReportRepository;

    @Mock
    private StationQuestionRepository stationQuestionRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private StationQuestionService stationQuestionService;

    @InjectMocks
    private TodaysReportService todaysReportService;

    @Test
    @DisplayName("createTodaysReport - 사용자가 정거장에 없을 때 null 반환")
    void createTodaysReport_userNotInStation_returnsNull() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(any(UserStationId.class))).thenReturn(Optional.empty());

        // when
        Report result = todaysReportService.createTodaysReport(email, stationPublicId);

        // then
        assertThat(result).isNull();
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("createTodaysReport - 이전에 답변한 적 있는 질문일 때 기존 리포트 반환")
    void createTodaysReport_existingReport_returnsExistingReport() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();
        Question question = Question.builder().id(1L).question("질문").build();

        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        UserStation userStation = UserStation.builder()
                .id(userStationId)
                .user(user)
                .station(station)
                .build();

        Report existingReport = Report.builder()
                .id(1L)
                .user(user)
                .question(question)
                .answer("답변")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(userStationId)).thenReturn(Optional.of(userStation));
        when(stationQuestionService.todaysQuestion(station)).thenReturn(question);
        when(reportRepository.findByUser(user)).thenReturn(List.of(existingReport));
        when(reportRepository.save(any(Report.class))).thenReturn(existingReport);
        when(stationReportRepository.save(any(StationReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Report result = todaysReportService.createTodaysReport(email, stationPublicId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(reportRepository).save(existingReport);
    }

    @Test
    @DisplayName("createTodaysReport - 새로운 리포트 생성")
    void createTodaysReport_newReport_createsReport() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();
        Question question = Question.builder().id(1L).question("질문").build();

        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        UserStation userStation = UserStation.builder()
                .id(userStationId)
                .user(user)
                .station(station)
                .build();

        Report newReport = Report.builder()
                .id(1L)
                .user(user)
                .question(question)
                .written(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(userStationRepository.findById(userStationId)).thenReturn(Optional.of(userStation));
        when(stationQuestionService.todaysQuestion(station)).thenReturn(question);
        when(reportRepository.findByUser(user)).thenReturn(new ArrayList<>());
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stationReportRepository.save(any(StationReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Report result = todaysReportService.createTodaysReport(email, stationPublicId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isWritten()).isFalse();
        verify(reportRepository).save(any(Report.class));
        verify(stationReportRepository).save(any(StationReport.class));
    }

    @Test
    @DisplayName("updateTodaysReport - 리포트가 없을 때 NONE 반환")
    void updateTodaysReport_reportNotFound_returnsNone() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        String answer = "답변";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().id("user1").build()));
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId)))
                .thenReturn(Optional.of(Station.builder().id(1L).build()));
        when(stationQuestionRepository.findByStationId(1L)).thenReturn(new ArrayList<>());

        // when
        ValidationStatus result = todaysReportService.updateTodaysReport(email, stationPublicId, answer);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NONE);
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("updateTodaysReport - 정상 케이스")
    void updateTodaysReport_validRequest_returnsValid() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        String answer = "답변";
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();
        Question question = Question.builder().id(1L).question("질문").build();

        Report report = Report.builder()
                .id(1L)
                .user(user)
                .question(question)
                .written(false)
                .build();

        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        UserStation userStation = UserStation.builder()
                .id(userStationId)
                .user(user)
                .station(station)
                .todayReport(false)
                .build();

        StationQuestion stationQuestion = StationQuestion.builder()
                .id(1L)
                .station(station)
                .question(question)
                .createdAt(LocalDate.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(stationQuestionRepository.findByStationId(station.getId())).thenReturn(List.of(stationQuestion));
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        when(reportRepository.findByUser(user)).thenReturn(List.of(report));
        when(stationReportRepository.findByStationIdAndReportId(station.getId(), report.getId()))
                .thenReturn(Optional.of(StationReport.builder().id(1L).build()));
        when(userStationRepository.findById(userStationId)).thenReturn(Optional.of(userStation));
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(userStationRepository.save(any(UserStation.class))).thenReturn(userStation);

        // when
        ValidationStatus result = todaysReportService.updateTodaysReport(email, stationPublicId, answer);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
        assertThat(report.getAnswer()).isEqualTo(answer);
        assertThat(report.isWritten()).isTrue();
        assertThat(userStation.isTodayReport()).isTrue();
        verify(reportRepository).save(report);
        verify(userStationRepository).save(userStation);
    }

    @Test
    @DisplayName("searchTodaysReport - 사용자가 존재하지 않을 때 예외 발생")
    void searchTodaysReport_userNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> todaysReportService.searchTodaysReport(email, stationPublicId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("searchTodaysReport - 정거장이 존재하지 않을 때 예외 발생")
    void searchTodaysReport_stationNotFound_throwsException() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> todaysReportService.searchTodaysReport(email, stationPublicId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STATION_NOT_FOUND);
    }

    @Test
    @DisplayName("searchTodaysReport - StationQuestion이 없을 때 null 반환")
    void searchTodaysReport_noStationQuestion_returnsNull() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(stationQuestionRepository.findByStationId(station.getId())).thenReturn(new ArrayList<>());

        // when
        Report result = todaysReportService.searchTodaysReport(email, stationPublicId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("searchTodaysReport - 정상 케이스")
    void searchTodaysReport_validRequest_returnsReport() {
        // given
        String email = "test@example.com";
        String stationPublicId = UUID.randomUUID().toString();
        User user = User.builder().id("user1").email(email).build();
        Station station = Station.builder().id(1L).build();
        Question question = Question.builder().id(1L).question("질문").build();

        Report report = Report.builder()
                .id(1L)
                .user(user)
                .question(question)
                .build();

        StationQuestion stationQuestion = StationQuestion.builder()
                .id(1L)
                .station(station)
                .question(question)
                .createdAt(LocalDate.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(stationRepository.findByPublicId(UUID.fromString(stationPublicId))).thenReturn(Optional.of(station));
        when(stationQuestionRepository.findByStationId(station.getId())).thenReturn(List.of(stationQuestion));
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        when(reportRepository.findByUser(user)).thenReturn(List.of(report));
        when(stationReportRepository.findByStationIdAndReportId(station.getId(), report.getId()))
                .thenReturn(Optional.of(StationReport.builder().id(1L).build()));

        // when
        Report result = todaysReportService.searchTodaysReport(email, stationPublicId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
}

