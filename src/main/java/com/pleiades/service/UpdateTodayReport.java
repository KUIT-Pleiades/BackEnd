package com.pleiades.service;

import com.pleiades.entity.Report;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.repository.ReportRepository;
import com.pleiades.repository.UserStationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateTodayReport {
    private final UserStationRepository userStationRepository;
    private final ReportRepository reportRepository;

    public UpdateTodayReport(UserStationRepository userStationRepository, ReportRepository reportRepository) {
        this.userStationRepository = userStationRepository;
        this.reportRepository = reportRepository;
    }

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
        List<UserStation> userStations = userStationRepository.findAll();
        for (UserStation userStation : userStations) {
            userStation.setTodayReport(false);
            userStationRepository.save(userStation);
        }
        System.out.println("TodayReport: false");
    }
}