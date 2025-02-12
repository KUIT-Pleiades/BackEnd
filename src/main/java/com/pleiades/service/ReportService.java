package com.pleiades.service;

import com.pleiades.dto.ReportDto;
import com.pleiades.entity.Report;
import com.pleiades.entity.User;
import com.pleiades.repository.QuestionRepository;
import com.pleiades.repository.ReportRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.ValidationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportService {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ReportRepository reportRepository;

    @Autowired
    ReportService(UserRepository userRepository, QuestionRepository questionRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.reportRepository = reportRepository;
    }

    public List<ReportDto> getAllReports(User user) {
        List<Report> reports = reportRepository.findByUser(user);
        List<ReportDto> reportDtos = new ArrayList<>();
        for (Report report : reports) {
            ReportDto reportDto = new ReportDto();

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
        List<Report> reports = reportRepository.findByUser(user);
        List<ReportDto> reportDtos = new ArrayList<>();

        for (Report report : reports) {
            if (report.getQuestion().getQuestion().contains(query)) {
                ReportDto reportDto = new ReportDto();
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
        List<Report> reports = reportRepository.findByUser(user);
        List<ReportDto> reportDtos = new ArrayList<>();

        for (Report report : reports) {
            if (report.getAnswer().contains(query)) {
                ReportDto reportDto = new ReportDto();
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
        List<ReportDto> questions = searchByQuestion(user, query);
        List<ReportDto> answers = searchByAnswer(user, query);

        Set<ReportDto> result = new HashSet<>(questions);
        result.addAll(answers);

        return result;
    }
}
