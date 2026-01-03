package com.pleiades.scheduler;

import com.pleiades.entity.Report;
import com.pleiades.repository.ReportRepository;
import com.pleiades.repository.UserStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UpdateTodayReport {
    private final UserStationRepository userStationRepository;
    private final ReportRepository reportRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul") // 매일 자정(00:00:00)에 실행
    public void eraseEmptyReport() {
        List<Report> reports = reportRepository.findAll();
        for (Report report : reports) {
            if (report.isWritten()) continue;
            reportRepository.delete(report);
        }
        System.out.println("Erased Empty Report");

        resetAllFields();   // 끝나고 실행
    }

    public void resetAllFields() {
        userStationRepository.resetTodaysReportToFalse();
        System.out.println("TodayReport: false");
    }
}