package com.pleiades.service;

import com.pleiades.dto.ReportDto;
import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.repository.*;
import com.pleiades.strings.ValidationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ReportService {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ReportRepository reportRepository;
    private final UserStationRepository userStationRepository;
    private final StationQuestionRepository stationQuestionRepository;

    @Autowired
    ReportService(UserRepository userRepository, QuestionRepository questionRepository, ReportRepository reportRepository, UserStationRepository userStationRepository, StationQuestionRepository stationQuestionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.reportRepository = reportRepository;
        this.userStationRepository = userStationRepository;
        this.stationQuestionRepository = stationQuestionRepository;
    }

    public List<ReportDto> getAllReports(User user) {
        List<Report> reports = reportRepository.findByUser(user);
        List<ReportDto> reportDtos = new ArrayList<>();
        for (Report report : reports) {
            ReportDto reportDto = new ReportDto();

            reportDto.setReportId(report.getId());
            reportDto.setQuestionId(report.getQuestion().getId());
            reportDto.setQuestion(report.getQuestion().getQuestion());
            reportDto.setAnswer(report.getAnswer());
            reportDto.setCreatedAt(report.getCreatedAt());
            reportDto.setModifiedAt(report.getModifiedAt());

            reportDtos.add(reportDto);
        }
        return reportDtos;
    }

    @Transactional
    public ValidationStatus updateReport(User user, Long reportId, String answer) {
        Optional<Report> report = reportRepository.findById(reportId);
        if (report.isEmpty()) { return ValidationStatus.NONE; }
        if (answer.isEmpty()) { return ValidationStatus.NOT_VALID; }

        report.get().setAnswer(answer);
        report.get().setModifiedAt(LocalDateTime.now());
        reportRepository.save(report.get());

        return ValidationStatus.VALID;
    }

    @Transactional
    public ValidationStatus deleteReport(User user, Long reportId) {
        Optional<Report> report = reportRepository.findById(reportId);
        if (report.isEmpty()) { return ValidationStatus.NONE; }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = report.get().getCreatedAt();

        Duration duration = Duration.between(createdAt, now);

        if (duration.toHours() < 24) { return ValidationStatus.NOT_VALID; }

        reportRepository.delete(report.get());
        return ValidationStatus.VALID;
    }

    public List<ReportDto> searchByQuestion(User user, String query) {
        log.info("searchByQuestion");
        List<Report> reports = reportRepository.findByUser(user);
        List<ReportDto> reportDtos = new ArrayList<>();

        for (Report report : reports) {
            if (report.getQuestion().getQuestion().contains(query)) {
                ReportDto reportDto = new ReportDto();
                reportDto.setReportId(report.getId());
                reportDto.setQuestionId(report.getQuestion().getId());
                reportDto.setQuestion(report.getQuestion().getQuestion());
                reportDto.setAnswer(report.getAnswer());
                reportDto.setCreatedAt(report.getCreatedAt());
                reportDto.setModifiedAt(report.getModifiedAt());

                reportDtos.add(reportDto);
            }
        }
        return reportDtos;
    }

    public List<ReportDto> searchByAnswer(User user, String query) {
        log.info("searchByAnswer");
        List<Report> reports = reportRepository.findByUser(user);
        List<ReportDto> reportDtos = new ArrayList<>();

        for (Report report : reports) {
            if (report.getAnswer().contains(query)) {
                ReportDto reportDto = new ReportDto();
                reportDto.setReportId(report.getId());
                reportDto.setQuestionId(report.getQuestion().getId());
                reportDto.setQuestion(report.getQuestion().getQuestion());
                reportDto.setAnswer(report.getAnswer());
                reportDto.setCreatedAt(report.getCreatedAt());
                reportDto.setModifiedAt(report.getModifiedAt());

                reportDtos.add(reportDto);
            }
        }
        return reportDtos;
    }

    public Set<ReportDto> searchResult(User user, String query) {
        log.info("Searching result for query {}", query);
        List<ReportDto> questions = searchByQuestion(user, query);
        List<ReportDto> answers = searchByAnswer(user, query);

        Set<ReportDto> result = new HashSet<>(questions);
        result.addAll(answers);

        log.info("result: {}", result);

        return result;
    }

    public Report searchTodaysReport(User user, String stationId) {
        log.info("searchTodaysReport");
        List<StationQuestion> stationQuestions = stationQuestionRepository.findByStationId(stationId);
        if (stationQuestions.isEmpty()) { return null; }

        Report report = null;

        for (StationQuestion stationQuestion : stationQuestions) {
            if (stationQuestion.getCreatedAt().equals(LocalDate.now())) {
                Question question = questionRepository.findById(stationQuestion.getQuestion().getId()).get();
                report = searchUserQuestion(user, question);
            }
        }

        return report;
    }

    public Question todaysQuestion(String stationId) {
        log.info("searchTodaysQuestion");
        List<StationQuestion> stationQuestions = stationQuestionRepository.findByStationId(stationId);
        if (stationQuestions.isEmpty()) { return null; }

        Question question = null;

        for (StationQuestion stationQuestion : stationQuestions) {
            if (stationQuestion.getCreatedAt().equals(LocalDate.now())) {
                question = questionRepository.findById(stationQuestion.getQuestion().getId()).get();
            }
        }

        if (question == null) { question = randomQuestion(); }

        return question;

    }

    // 오늘 리포트를 안 쓴 건지 - 이건 메서드 밖에서 검증하는 게 좋을 듯 - userStation의 todayReport가 false일 때만 호출
    // 해당 질문을 이전에 답변한 적 있는지 그럼 오늘의 리포트가 아니잖아..true자나... 젠장... false로 바꿔?..
    public Report createReport(User user, Station station) {
        log.info("createReport");
        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        Optional<UserStation> userStation = userStationRepository.findById(userStationId);
        if (userStation.isEmpty()) { log.info("user not in station"); return null; }

        Question question = todaysQuestion(station.getId());

        Report existingReport = searchUserQuestion(user, question);
        // 이전에 답변한 적 있는 질문
        if (existingReport != null) { return existingReport; }

        Report report = Report.builder().user(user).written(false).createdAt(LocalDateTime.now()).build();
        reportRepository.save(report);

        return report;
    }

    private Question randomQuestion() {
        Random random = new Random();
        long questionNum = random.nextInt(100) + 1;
        Optional<Question> question = questionRepository.findById(questionNum);
        return question.orElse(null);
    }

    public Report searchUserQuestion(User user, Question question) {
        log.info("searchUserQuestion");
        List<Report> reports = reportRepository.findByUser(user);
        for (Report report : reports) {
            if (report.getQuestion().getQuestion().equals(question.getQuestion())) { return report; }
        }
        return null;
    }
}
