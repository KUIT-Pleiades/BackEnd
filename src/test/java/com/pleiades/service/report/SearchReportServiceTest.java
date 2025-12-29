package com.pleiades.service.report;

import com.pleiades.dto.ReportDto;
import com.pleiades.entity.Question;
import com.pleiades.entity.Report;
import com.pleiades.entity.User;
import com.pleiades.repository.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SearchReportService searchReportService;

    @Test
    @DisplayName("searchResult - 질문으로 검색")
    void searchResult_searchByQuestion_returnsMatchingReports() {
        // given
        User user = User.builder().id("user1").build();
        String query = "질문";
        Question question1 = Question.builder().id(1L).question("질문 내용").build();
        Question question2 = Question.builder().id(2L).question("다른 내용").build();

        Report report1 = Report.builder()
                .id(1L)
                .user(user)
                .question(question1)
                .answer("답변")
                .createdAt(LocalDateTime.now())
                .build();

        Report report2 = Report.builder()
                .id(2L)
                .user(user)
                .question(question2)
                .answer("답변")
                .createdAt(LocalDateTime.now())
                .build();

        ReportDto dto1 = new ReportDto();
        dto1.setReportId(1L);
        dto1.setQuestion("질문 내용");

        when(reportRepository.findByUser(user)).thenReturn(List.of(report1, report2));
        when(modelMapper.map(report1, ReportDto.class)).thenReturn(dto1);

        // when
        Set<ReportDto> result = searchReportService.searchResult(user, query);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getReportId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("searchResult - 답변으로 검색")
    void searchResult_searchByAnswer_returnsMatchingReports() {
        // given
        User user = User.builder().id("user1").build();
        String query = "답변";
        Question question = Question.builder().id(1L).question("질문").build();

        Report report1 = Report.builder()
                .id(1L)
                .user(user)
                .question(question)
                .answer("답변 내용")
                .createdAt(LocalDateTime.now())
                .build();

        Report report2 = Report.builder()
                .id(2L)
                .user(user)
                .question(question)
                .answer("다른 내용")
                .createdAt(LocalDateTime.now())
                .build();

        ReportDto dto1 = new ReportDto();
        dto1.setReportId(1L);
        dto1.setAnswer("답변 내용");

        when(reportRepository.findByUser(user)).thenReturn(List.of(report1, report2));
        when(modelMapper.map(report1, ReportDto.class)).thenReturn(dto1);

        // when
        Set<ReportDto> result = searchReportService.searchResult(user, query);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getReportId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("searchResult - 답변이 null인 리포트는 제외")
    void searchResult_nullAnswer_excludesFromSearch() {
        // given
        User user = User.builder().id("user1").build();
        String query = "답변";
        Question question = Question.builder().id(1L).question("질문").build();

        Report report = Report.builder()
                .id(1L)
                .user(user)
                .question(question)
                .answer(null)
                .createdAt(LocalDateTime.now())
                .build();

        when(reportRepository.findByUser(user)).thenReturn(List.of(report));

        // when
        Set<ReportDto> result = searchReportService.searchResult(user, query);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("searchResult - 질문과 답변 모두 매칭되는 경우 중복 제거")
    void searchResult_bothMatch_removesDuplicates() {
        // given
        User user = User.builder().id("user1").build();
        String query = "공통";
        Question question = Question.builder().id(1L).question("공통 질문").build();

        Report report = Report.builder()
                .id(1L)
                .user(user)
                .question(question)
                .answer("공통 답변")
                .createdAt(LocalDateTime.now())
                .build();

        ReportDto dto = new ReportDto();
        dto.setReportId(1L);

        when(reportRepository.findByUser(user)).thenReturn(List.of(report));
        when(modelMapper.map(report, ReportDto.class)).thenReturn(dto);

        // when
        Set<ReportDto> result = searchReportService.searchResult(user, query);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("searchResult - 빈 쿼리")
    void searchResult_emptyQuery_returnsEmptySet() {
        // given
        User user = User.builder().id("user1").build();
        String query = "";

        when(reportRepository.findByUser(user)).thenReturn(List.of());

        // when
        Set<ReportDto> result = searchReportService.searchResult(user, query);

        // then
        assertThat(result).isEmpty();
    }
}

