package com.pleiades.service;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.ReportListDto;
import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.repository.*;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class ReportService {
    private final QuestionRepository questionRepository;
    private final ReportRepository reportRepository;
    private final UserStationRepository userStationRepository;
    private final StationQuestionRepository stationQuestionRepository;
    private final StationReportRepository stationReportRepository;

    @Autowired
    ReportService(QuestionRepository questionRepository, ReportRepository reportRepository, UserStationRepository userStationRepository, StationQuestionRepository stationQuestionRepository, StationReportRepository stationReportRepository) {
        this.questionRepository = questionRepository;
        this.reportRepository = reportRepository;
        this.userStationRepository = userStationRepository;
        this.stationQuestionRepository = stationQuestionRepository;
        this.stationReportRepository = stationReportRepository;
    }

    public List<ReportDto> getAllReports(User user) {
        List<Report> reports = reportRepository.findByUserOrderByCreatedAtDesc(user);
        List<ReportDto> reportDtos = new ArrayList<>();
        for (Report report : reports) {
            if (!report.isWritten()) continue;
            ReportDto reportDto = reportToDto(report);
            ReportListDto reportListDto = new ReportListDto(reportDto);
            reportListDto.setIsTodayReport(isTodayReport(report));

            reportDtos.add(reportListDto);
        }
        return reportDtos;
    }

    @Transactional
    public ValidationStatus updateReport(User user, Long reportId, String answer) {
        Optional<Report> report = reportRepository.findById(reportId);
        if (report.isEmpty()) { return ValidationStatus.NONE; }

        if (!report.get().getUser().equals(user)) { return ValidationStatus.NOT_VALID; }

        report.get().setAnswer(answer);
        report.get().setModifiedAt(LocalDateTimeUtil.now());
        reportRepository.save(report.get());

        return ValidationStatus.VALID;
    }

    @Transactional
    public ValidationStatus deleteReport(User user, Long reportId) {
        Optional<Report> report = reportRepository.findById(reportId);
        if (report.isEmpty()) { return ValidationStatus.NONE; }

        if (!report.get().getUser().equals(user)) { return ValidationStatus.NOT_VALID; }

        if (isTodayReport(report.get())) { return ValidationStatus.DUPLICATE; }

        reportRepository.delete(report.get());
        return ValidationStatus.VALID;
    }

    public List<ReportDto> searchByQuestion(User user, String query) {
        log.info("searchByQuestion");
        List<Report> reports = reportRepository.findByUser(user);
        List<ReportDto> reportDtos = new ArrayList<>();

        for (Report report : reports) {
            if (report.getQuestion().getQuestion().contains(query)) {
                ReportDto reportDto = reportToDto(report);
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
            if (report.getAnswer() == null) continue;
            if (report.getAnswer().contains(query)) {
                ReportDto reportDto = reportToDto(report);
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

    // 이 정거장에서 해당 사용자가 작성한 투데이 리포트 반환
    public Report searchTodaysReport(User user, Station station) {
        log.info("searchTodaysReport");
        List<StationQuestion> stationQuestions = stationQuestionRepository.findByStationId(station.getId());
        if (stationQuestions.isEmpty()) { return null; }

        Report report = null;

        for (StationQuestion stationQuestion : stationQuestions) {
            if (stationQuestion.getCreatedAt().equals(LocalDateTimeUtil.today())) {
                Question question = questionRepository.findById(stationQuestion.getQuestion().getId()).get();
                report = searchUserQuestion(user, question);
                if (report == null) { return report; }
                Optional<StationReport> stationReport = stationReportRepository.findByStationIdAndReportId(station.getId(), report.getId());
                if (stationReport.isEmpty()) { return null; }
            }
        }

        return report;
    }

    // 정거장에 그 날 처음 들어온 사람이 호출
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

    // 투데이 리포트 생성 (생성만, 작성은 안 됨)
    public Report createReport(User user, Station station) {
        log.info("createReport");
        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        Optional<UserStation> userStation = userStationRepository.findById(userStationId);
        if (userStation.isEmpty()) { log.info("user not in station"); return null; }

        Question question = todaysQuestion(station);

        Report existingReport = searchUserQuestion(user, question);
        StationReport stationReport = new StationReport();
        stationReport.setStation(station);

        // 이전에 답변한 적 있는 질문
        if (existingReport != null) {
            reportRepository.save(existingReport);
            stationReport.setReport(existingReport);
            stationReportRepository.save(stationReport);

            return existingReport;
        }

        Report report = Report.builder().user(user).question(question).written(false).createdAt(LocalDateTimeUtil.now()).build();
        reportRepository.save(report);
        stationReport.setReport(report);
        stationReportRepository.save(stationReport);

        return report;
    }

    private Question randomQuestion() {
        Random random = new Random();
        long questionNum = random.nextInt(100) + 1;
        Optional<Question> question = questionRepository.findById(questionNum);
        return question.orElse(null);
    }

    // 해당 사용자가 헤당 질문에 대해 답변한 리포트 반환
    public Report searchUserQuestion(User user, Question question) {
        log.info("searchUserQuestion");
        List<Report> reports = reportRepository.findByUser(user);
        for (Report report : reports) {
            if (report.getQuestion().getQuestion().equals(question.getQuestion())) { return report; }
        }
        return null;
    }

    // 걍 업데이트랑 뭐가 다름? UserStation -> true 가 다름
    public ValidationStatus updateTodaysReport(User user, Station station, String answer) {
        log.info("updateTodaysReport");

        // 사용자가 오늘의 리포트를 생성한 적 없음
        Report report = searchTodaysReport(user, station);
        if (report == null) { return ValidationStatus.NONE; }

        report.setAnswer(answer);
        report.setModifiedAt(LocalDateTimeUtil.now());
        report.setWritten(true);
        reportRepository.save(report);

        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        Optional<UserStation> userStation = userStationRepository.findById(userStationId);

        userStation.get().setTodayReport(true);
        userStationRepository.save(userStation.get());

        return ValidationStatus.VALID;
    }

    public Boolean isTodayReport(Report report) {
        log.info("isTodayReport");
        if (report == null) { log.info("no report"); return false; }
        if (report.getModifiedAt() == null) { log.info("not modified"); return false; }
        if (!report.getModifiedAt().toLocalDate().equals(LocalDateTimeUtil.today())) { log.info("localDate: {}", report.getModifiedAt().toLocalDate()); log.info("today: {}", LocalDateTimeUtil.today()); return false; }

        List<StationReport> stationReports = stationReportRepository.findByReportId(report.getId());
        for (StationReport stationReport : stationReports) {
            if (todaysQuestion(stationReport.getStation()).equals(report.getQuestion())) { log.info("is today's report"); return true; }
        }
        log.info("is not today's report");
        return false;
    }

    public ReportDto reportToDto(Report report) {
        log.info("reportToDto");
        ReportDto reportDto = new ReportDto();

        reportDto.setReportId(report.getId());
        reportDto.setQuestionId(report.getQuestion().getId());
        reportDto.setQuestion(report.getQuestion().getQuestion());
        reportDto.setAnswer(report.getAnswer());
        reportDto.setCreatedAt(report.getCreatedAt());
        reportDto.setModifiedAt(report.getModifiedAt());

        return reportDto;
    }
}
