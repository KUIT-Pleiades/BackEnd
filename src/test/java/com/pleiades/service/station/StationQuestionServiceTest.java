package com.pleiades.service.station;

import com.pleiades.entity.Question;
import com.pleiades.entity.Station;
import com.pleiades.entity.StationQuestion;
import com.pleiades.repository.QuestionRepository;
import com.pleiades.repository.StationQuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationQuestionServiceTest {

    @Mock
    private StationQuestionRepository stationQuestionRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private StationQuestionService stationQuestionService;

    @Test
    @DisplayName("todaysQuestion - StationQuestion이 없을 때 새로 생성")
    void todaysQuestion_noStationQuestion_createsNew() {
        // given
        Station station = Station.builder().id(1L).build();
        Question question = Question.builder().id(1L).question("질문").build();

        when(stationQuestionRepository.findByStationId(station.getId())).thenReturn(new ArrayList<>());
        when(questionRepository.count()).thenReturn(100L);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(stationQuestionRepository.findByStationIdAndQuestionId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(stationQuestionRepository.save(any(StationQuestion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Question result = stationQuestionService.todaysQuestion(station);

        // then
        assertThat(result).isNotNull();
        verify(stationQuestionRepository).save(any(StationQuestion.class));
    }

    @Test
    @DisplayName("todaysQuestion - 오늘의 질문이 존재할 때 반환")
    void todaysQuestion_todaysQuestionExists_returnsQuestion() {
        // given
        Station station = Station.builder().id(1L).build();
        Question question = Question.builder().id(1L).question("질문").build();

        StationQuestion stationQuestion = StationQuestion.builder()
                .id(1L)
                .station(station)
                .question(question)
                .createdAt(LocalDate.now())
                .build();

        when(stationQuestionRepository.findByStationId(station.getId())).thenReturn(List.of(stationQuestion));
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        // when
        Question result = stationQuestionService.todaysQuestion(station);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(stationQuestionRepository, never()).save(any(StationQuestion.class));
    }

    @Test
    @DisplayName("todaysQuestion - 오늘의 질문이 없을 때 새로 생성")
    void todaysQuestion_noTodaysQuestion_createsNew() {
        // given
        Station station = Station.builder().id(1L).build();
        Question oldQuestion = Question.builder().id(1L).question("어제 질문").build();
        Question newQuestion = Question.builder().id(2L).question("오늘 질문").build();

        StationQuestion oldStationQuestion = StationQuestion.builder()
                .id(1L)
                .station(station)
                .question(oldQuestion)
                .createdAt(LocalDate.now().minusDays(1))
                .build();

        when(stationQuestionRepository.findByStationId(station.getId())).thenReturn(List.of(oldStationQuestion));
        when(questionRepository.count()).thenReturn(100L);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(newQuestion));
        when(stationQuestionRepository.findByStationIdAndQuestionId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(stationQuestionRepository.save(any(StationQuestion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Question result = stationQuestionService.todaysQuestion(station);

        // then
        assertThat(result).isNotNull();
        verify(stationQuestionRepository).save(any(StationQuestion.class));
    }
}

