package com.pleiades.service.report;

import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.service.auth.AuthService;
import com.pleiades.service.station.StationQuestionService;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TodaysReportService {
    // User의 Todays Report에 관한 Service

    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final UserStationRepository userStationRepository;
    private final ReportRepository reportRepository;
    private final StationReportRepository stationReportRepository;
    private final StationQuestionRepository stationQuestionRepository;
    private final QuestionRepository questionRepository;

    private final StationQuestionService stationQuestionService;
    private final AuthService authService;

    // 투데이 리포트 생성 (생성만, 작성은 안 됨)
    public Report createTodaysReport(String email, String stationPublicId) {
        log.info("createReport");
        authService.userInStation(stationPublicId, email);

        User user = userRepository.findByEmail(email).get();
        Station station = stationRepository.findByPublicId(UUID.fromString(stationPublicId)).get();

        UserStationId userStationId = new UserStationId(user.getId(), station.getId());
        Optional<UserStation> userStation = userStationRepository.findById(userStationId);
        if (userStation.isEmpty()) { log.info("user not in station"); return null; }

        Question question = stationQuestionService.todaysQuestion(station);    // 정거장에 그 날 처음 들어온 사람이 호출

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

    // 걍 업데이트랑 뭐가 다름? UserStation -> true 가 다름
    public ValidationStatus updateTodaysReport(String email, String stationPublicId, String answer) {
        log.info("updateTodaysReport");

        authService.userInStation(stationPublicId, email);

        // 사용자가 오늘의 리포트를 생성한 적 없음
        Report report = searchTodaysReportById(email, stationPublicId);
        if (report == null) { return ValidationStatus.NONE; }

        User user = userRepository.findByEmail(email).get();
        Station station = stationRepository.findByPublicId(UUID.fromString(stationPublicId)).get();

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

    // 이 정거장에서 해당 사용자가 작성한 투데이 리포트 반환
    public Report searchTodaysReportById(String email, String stationPublicId) {
        log.info("searchTodaysReportById");

        authService.userInStation(stationPublicId, email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Station station = stationRepository.findByPublicId(UUID.fromString(stationPublicId)).orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND));

        List<StationQuestion> stationQuestions = stationQuestionRepository.findByStationId(station.getId());
        if (stationQuestions.isEmpty()) { return null; }

        Report report = null;

        for (StationQuestion stationQuestion : stationQuestions) {
            if (stationQuestion.getCreatedAt().equals(LocalDateTimeUtil.today())) {
                Question question = questionRepository.findById(stationQuestion.getQuestion().getId()).orElseThrow(()->new CustomException(ErrorCode.NO_TODAYS_REPORT));
                report = searchUserQuestion(user, question);
                if (report == null) { return report; }
                Optional<StationReport> stationReport = stationReportRepository.findByStationIdAndReportId(station.getId(), report.getId());
                if (stationReport.isEmpty()) { return null; }
            }
        }

        return report;
    }

    public Report searchTodaysReportByCode(String email, String stationCode) {
        log.info("searchTodaysReportByCode");

        authService.userInStation(stationCode, email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Station station = stationRepository.findByCode(stationCode).orElseThrow(() -> new CustomException(ErrorCode.STATION_NOT_FOUND));

        List<StationQuestion> stationQuestions = stationQuestionRepository.findByStationId(station.getId());
        if (stationQuestions.isEmpty()) { return null; }

        Report report = null;

        for (StationQuestion stationQuestion : stationQuestions) {
            if (stationQuestion.getCreatedAt().equals(LocalDateTimeUtil.today())) {
                Question question = questionRepository.findById(stationQuestion.getQuestion().getId()).orElseThrow(()->new CustomException(ErrorCode.NO_TODAYS_REPORT));
                report = searchUserQuestion(user, question);
                if (report == null) { return report; }
                Optional<StationReport> stationReport = stationReportRepository.findByStationIdAndReportId(station.getId(), report.getId());
                if (stationReport.isEmpty()) { return null; }
            }
        }

        return report;
    }

    // 해당 사용자가 헤당 질문에 대해 답변한 리포트 반환
    private Report searchUserQuestion(User user, Question question) {
        log.info("searchUserQuestion");
        List<Report> reports = reportRepository.findByUser(user);
        for (Report report : reports) {
            if (report.getQuestion().getQuestion().equals(question.getQuestion())) { return report; }
        }
        return null;
    }
}
