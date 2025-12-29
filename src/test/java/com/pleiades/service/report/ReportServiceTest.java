package com.pleiades.service.report;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.ReportListDto;
import com.pleiades.entity.*;
import com.pleiades.repository.ReportRepository;
import com.pleiades.repository.StationReportRepository;
import com.pleiades.service.station.StationQuestionService;
import com.pleiades.strings.ValidationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private StationReportRepository stationReportRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private StationQuestionService stationQuestionService;

    @InjectMocks
    private ReportService reportService;

    @Test
    @DisplayName("getAllReports - written이 false인 리포트는 제외")
    void getAllReports_excludesUnwrittenReports() {
        // given
        User user = User.builder().id("user1").build();
        Question question = Question.builder().id(1L).question("질문").build();

        Report writtenReport = Report.builder()
                .id(1L)
                .user(user)
                .question(question)
                .answer("답변")
                .written(true)
                .createdAt(LocalDateTime.now())
                .build();

        Report unwrittenReport = Report.builder()
                .id(2L)
                .user(user)
                .question(question)
                .written(false)
                .createdAt(LocalDateTime.now())
                .build();

        List<Report> reports = List.of(writtenReport, unwrittenReport);
        ReportListDto dto = new ReportListDto();
        dto.setReportId(1L);
        dto.setQuestion("질문");

        when(reportRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(reports);
        when(modelMapper.map(writtenReport, ReportListDto.class)).thenReturn(dto);
        when(stationReportRepository.findByReportId(1L)).thenReturn(new ArrayList<>());

        // when
        List<ReportDto> result = reportService.getAllReports(user);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReportId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("updateReport - 리포트가 존재하지 않을 때 NONE 반환")
    void updateReport_reportNotFound_returnsNone() {
        // given
        User user = User.builder().id("user1").build();
        Long reportId = 1L;
        String answer = "답변";

        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // when
        ValidationStatus result = reportService.updateReport(user, reportId, answer);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NONE);
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("updateReport - 사용자가 리포트 소유자가 아닐 때 NOT_VALID 반환")
    void updateReport_userNotOwner_returnsNotValid() {
        // given
        User user = User.builder().id("user1").build();
        User otherUser = User.builder().id("user2").build();
        Long reportId = 1L;
        String answer = "답변";

        Question question = Question.builder().id(1L).question("질문").build();
        Report report = Report.builder()
                .id(reportId)
                .user(otherUser)
                .question(question)
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        // when
        ValidationStatus result = reportService.updateReport(user, reportId, answer);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("updateReport - 정상 케이스")
    void updateReport_validRequest_returnsValid() {
        // given
        User user = User.builder().id("user1").build();
        Long reportId = 1L;
        String answer = "답변";

        Question question = Question.builder().id(1L).question("질문").build();
        Report report = Report.builder()
                .id(reportId)
                .user(user)
                .question(question)
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        // when
        ValidationStatus result = reportService.updateReport(user, reportId, answer);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
        assertThat(report.getAnswer()).isEqualTo(answer);
        verify(reportRepository).save(report);
    }

    @Test
    @DisplayName("deleteReport - 리포트가 존재하지 않을 때 NONE 반환")
    void deleteReport_reportNotFound_returnsNone() {
        // given
        User user = User.builder().id("user1").build();
        Long reportId = 1L;

        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // when
        ValidationStatus result = reportService.deleteReport(user, reportId);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NONE);
        verify(reportRepository, never()).delete(any(Report.class));
    }

    @Test
    @DisplayName("deleteReport - 사용자가 리포트 소유자가 아닐 때 NOT_VALID 반환")
    void deleteReport_userNotOwner_returnsNotValid() {
        // given
        User user = User.builder().id("user1").build();
        User otherUser = User.builder().id("user2").build();
        Long reportId = 1L;

        Question question = Question.builder().id(1L).question("질문").build();
        Report report = Report.builder()
                .id(reportId)
                .user(otherUser)
                .question(question)
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        // when
        ValidationStatus result = reportService.deleteReport(user, reportId);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
        verify(reportRepository, never()).delete(any(Report.class));
    }

    @Test
    @DisplayName("deleteReport - 오늘의 리포트일 때 DUPLICATE 반환")
    void deleteReport_todaysReport_returnsDuplicate() {
        // given
        User user = User.builder().id("user1").build();
        Long reportId = 1L;
        Station station = Station.builder().id(1L).build();
        Question question = Question.builder().id(1L).question("질문").build();

        Report report = Report.builder()
                .id(reportId)
                .user(user)
                .question(question)
                .modifiedAt(LocalDateTime.now())
                .build();

        StationReport stationReport = StationReport.builder()
                .id(1L)
                .station(station)
                .report(report)
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(stationReportRepository.findByReportId(reportId)).thenReturn(List.of(stationReport));
        when(stationQuestionService.todaysQuestion(station)).thenReturn(question);

        // when
        ValidationStatus result = reportService.deleteReport(user, reportId);

        // then
        assertThat(result).isEqualTo(ValidationStatus.DUPLICATE);
        verify(reportRepository, never()).delete(any(Report.class));
    }

    @Test
    @DisplayName("deleteReport - 정상 케이스")
    void deleteReport_validRequest_returnsValid() {
        // given
        User user = User.builder().id("user1").build();
        Long reportId = 1L;
        Question question = Question.builder().id(1L).question("질문").build();

        Report report = Report.builder()
                .id(reportId)
                .user(user)
                .question(question)
                .modifiedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(stationReportRepository.findByReportId(reportId)).thenReturn(new ArrayList<>());
        doNothing().when(reportRepository).delete(report);

        // when
        ValidationStatus result = reportService.deleteReport(user, reportId);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
        verify(reportRepository).delete(report);
    }
}

