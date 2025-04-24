package com.pleiades.service;

import com.pleiades.entity.Question;
import com.pleiades.entity.Station;
import com.pleiades.entity.StationQuestion;
import com.pleiades.repository.QuestionRepository;
import com.pleiades.repository.StationQuestionRepository;
import com.pleiades.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class StationQuestionService {
    // Station의 오늘의 Question에 관한 Service

    private final StationQuestionRepository stationQuestionRepository;
    private final QuestionRepository questionRepository;

    // Station의 오늘의 질문을 가져옴
    // 없으면 생성: 외부에선 생성이 됐는지 신경 쓸 필요 없음. 없으면 서비스 내에서 생성해서 반환할 것임
    public Question todaysQuestion(Station station) {
        log.info("searchTodaysQuestion");
        List<StationQuestion> stationQuestions = stationQuestionRepository.findByStationId(station.getId());
        if (stationQuestions.isEmpty()) { return setTodaysStationQuesiton(station); }

        Question question = null;

        for (StationQuestion stationQuestion : stationQuestions) {
            if (stationQuestion.getCreatedAt().equals(LocalDateTimeUtil.today())) {
                log.info("stationQuestion: {}", stationQuestion);
                question = questionRepository.findById(stationQuestion.getQuestion().getId()).get();
            }
        }

        log.info("question: {}", question);
        if (question == null) { return setTodaysStationQuesiton(station); }

        return question;
    }

    // 정거장의 오늘의 질문 설정
    private Question setTodaysStationQuesiton(Station station) {
        Question question = null;
        Optional<StationQuestion> existingStationQuesiton;
        int count = 0;
        do {
            count++;
            question = randomQuestion();
            existingStationQuesiton = stationQuestionRepository.findByStationIdAndQuestiontId(station.getId(), question.getId());
        } while (existingStationQuesiton.isPresent() && count <= questionRepository.count());

        // 더 이상 할당 가능한 질문이 없으면 어떡하지
        if (count > questionRepository.count()) {
            question = new Question();
            question.setQuestion("!!!!!우주의 종말!!!!!");
            return question;
        }

        StationQuestion stationQuestion = new StationQuestion();
        stationQuestion.setStation(station);
        stationQuestion.setQuestion(question);
        stationQuestion.setCreatedAt(LocalDateTimeUtil.today());
        stationQuestionRepository.save(stationQuestion);

        return question;
    }

    private Question randomQuestion() {
        Random random = new Random();
        long questionNum = random.nextInt(100) + 1;
        Optional<Question> question = questionRepository.findById(questionNum);
        return question.orElse(null);
    }
}
