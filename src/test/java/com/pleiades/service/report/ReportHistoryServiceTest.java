package com.pleiades.service.report;

import com.pleiades.dto.ReportHistoryDto;
import com.pleiades.entity.ReportHistory;
import com.pleiades.entity.User;
import com.pleiades.repository.ReportHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportHistoryServiceTest {

    @Mock
    private ReportHistoryRepository reportHistoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReportHistoryService reportHistoryService;

    @Test
    @DisplayName("saveReportHistory - 기존 히스토리가 존재할 때 업데이트")
    void saveReportHistory_existingHistory_updatesHistory() {
        // given
        String query = "검색어";
        User user = User.builder().id("user1").build();

        ReportHistory existingHistory = ReportHistory.builder()
                .id(1L)
                .query(query.trim())
                .user(user)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        when(reportHistoryRepository.findByQuery(query.trim())).thenReturn(Optional.of(existingHistory));
        when(reportHistoryRepository.save(any(ReportHistory.class))).thenReturn(existingHistory);

        // when
        reportHistoryService.saveReportHistory(query, user);

        // then
        verify(reportHistoryRepository).save(existingHistory);
        assertThat(existingHistory.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("saveReportHistory - 새로운 히스토리 생성")
    void saveReportHistory_newHistory_createsHistory() {
        // given
        String query = "새 검색어";
        User user = User.builder().id("user1").build();

        ReportHistory newHistory = ReportHistory.builder()
                .id(1L)
                .query(query.trim())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(reportHistoryRepository.findByQuery(query.trim())).thenReturn(Optional.empty());
        when(reportHistoryRepository.save(any(ReportHistory.class))).thenReturn(newHistory);

        // when
        reportHistoryService.saveReportHistory(query, user);

        // then
        verify(reportHistoryRepository).save(any(ReportHistory.class));
    }

    @Test
    @DisplayName("saveReportHistory - 공백이 있는 쿼리 처리")
    void saveReportHistory_queryWithSpaces_trimsQuery() {
        // given
        String query = "  검색어  ";
        User user = User.builder().id("user1").build();

        when(reportHistoryRepository.findByQuery("검색어")).thenReturn(Optional.empty());
        when(reportHistoryRepository.save(any(ReportHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        reportHistoryService.saveReportHistory(query, user);

        // then
        verify(reportHistoryRepository).findByQuery("검색어");
    }

    @Test
    @DisplayName("deleteIfOverTen - 10개 이하일 때 삭제하지 않음")
    void deleteIfOverTen_tenOrLess_doesNotDelete() {
        // given
        User user = User.builder().id("user1").build();
        List<ReportHistory> histories = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            histories.add(ReportHistory.builder().id((long) i).build());
        }

        when(reportHistoryRepository.findByUser(user)).thenReturn(histories);

        // when
        reportHistoryService.deleteIfOverTen(user);

        // then
        verify(reportHistoryRepository, never()).delete(any(ReportHistory.class));
    }

    @Test
    @DisplayName("deleteIfOverTen - 10개 초과일 때 가장 오래된 항목 삭제")
    void deleteIfOverTen_moreThanTen_deletesOldest() {
        // given
        User user = User.builder().id("user1").build();
        List<ReportHistory> histories = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 11; i++) {
            ReportHistory history = ReportHistory.builder()
                    .id((long) i)
                    .createdAt(now.minusDays(i))
                    .build();
            histories.add(history);
        }

        when(reportHistoryRepository.findByUser(user)).thenReturn(histories);
        doNothing().when(reportHistoryRepository).delete(any(ReportHistory.class));

        // when
        reportHistoryService.deleteIfOverTen(user);

        // then
        verify(reportHistoryRepository).delete(any(ReportHistory.class));
    }

    @Test
    @DisplayName("deleteById - 히스토리가 존재하지 않을 때 NOT_FOUND 반환")
    void deleteById_historyNotFound_returnsNotFound() {
        // given
        Long id = 1L;

        when(reportHistoryRepository.findById(id)).thenReturn(Optional.empty());

        // when
        ResponseEntity<Map<String, Object>> response = reportHistoryService.deleteById(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "No history with id " + id + " found");
        verify(reportHistoryRepository, never()).delete(any(ReportHistory.class));
    }

    @Test
    @DisplayName("deleteById - 정상 케이스")
    void deleteById_validId_returnsOk() {
        // given
        Long id = 1L;
        ReportHistory history = ReportHistory.builder()
                .id(id)
                .query("검색어")
                .createdAt(LocalDateTime.now())
                .build();

        ReportHistoryDto dto = new ReportHistoryDto();
        dto.setId(id);
        dto.setQuery("검색어");

        when(reportHistoryRepository.findById(id)).thenReturn(Optional.of(history));
        when(modelMapper.map(history, ReportHistoryDto.class)).thenReturn(dto);
        doNothing().when(reportHistoryRepository).delete(history);

        // when
        ResponseEntity<Map<String, Object>> response = reportHistoryService.deleteById(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Report history deleted");
        assertThat(response.getBody()).containsKey("history");
        verify(reportHistoryRepository).delete(history);
    }
}

