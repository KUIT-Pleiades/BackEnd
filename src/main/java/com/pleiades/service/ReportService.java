package com.pleiades.service;

import com.pleiades.dto.ReportDto;
import com.pleiades.dto.ReportListDto;
import com.pleiades.entity.*;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.repository.*;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final StationReportRepository stationReportRepository;
    private final ModelMapper modelMapper;
    private final StationQuestionService stationQuestionService;

    public List<ReportDto> getAllReports(User user) {
        List<Report> reports = reportRepository.findByUserOrderByCreatedAtDesc(user);
        List<ReportDto> reportDtos = new ArrayList<>();
        for (Report report : reports) {
            if (!report.isWritten()) continue;

            ReportListDto reportListDto = modelMapper.map(report, ReportListDto.class);
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

    private Boolean isTodayReport(Report report) {
        log.info("isTodayReport");
        if (report == null) { log.info("no report"); return false; }
        if (report.getModifiedAt() == null) { log.info("not modified"); return false; }
        if (!report.getModifiedAt().toLocalDate().equals(LocalDateTimeUtil.today())) { log.info("localDate: {}", report.getModifiedAt().toLocalDate()); log.info("today: {}", LocalDateTimeUtil.today()); return false; }

        List<StationReport> stationReports = stationReportRepository.findByReportId(report.getId());
        for (StationReport stationReport : stationReports) {
            if (stationQuestionService.todaysQuestion(stationReport.getStation()).equals(report.getQuestion())) { log.info("is today's report"); return true; }
        }
        log.info("is not today's report");
        return false;
    }
}
