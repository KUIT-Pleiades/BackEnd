package com.pleiades.service;

import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.repository.UserStationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateTodayReport {
    private final UserStationRepository userStationRepository;
    private List<UserStation> userStations;

    public UpdateTodayReport(UserStationRepository userStationRepository) {
        userStations = userStationRepository.findAll();
        this.userStationRepository = userStationRepository;
    }

    @Scheduled(cron = "0 13 2 * * ?", zone = "Asia/Seoul") // 매일 자정(00:00:00)에 실행
    public void resetAllFields() {
        userStations = userStationRepository.findAll();
        for (UserStation userStation : userStations) {
            userStation.setTodayReport(false);
            userStationRepository.save(userStation);
        }
        System.out.println("TodayReport: false");
    }
}