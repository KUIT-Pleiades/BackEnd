package com.pleiades.service;

import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.repository.UserStationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class UpdateTodayReport {
    private final UserStationRepository userStationRepository;
    private List<UserStation> userStations = new CopyOnWriteArrayList<>();


    public UpdateTodayReport(UserStationRepository userStationRepository) {
        userStations = userStationRepository.findAll();
        this.userStationRepository = userStationRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정(00:00:00)에 실행
    public void resetAllFields() {
        List<UserStation> instances = userStationRepository.findAll();
        userStations.addAll(instances);
        for (UserStation userStation : userStations) {
            userStation.setTodayReport(false);
            userStationRepository.save(userStation);
        }
        System.out.println("TodayReport: false");
    }
}