package com.pleiades.scheduler;

import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import com.pleiades.repository.StationRepository;
import com.pleiades.repository.UserStationRepository;
import com.pleiades.service.FcmService;
import com.pleiades.strings.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReportNoticeScheduler {

    private final StationRepository stationRepository;
    private final UserStationRepository userStationRepository;
    private final FcmService fcmService;

    @Transactional
    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Seoul") // 매분 실행
    public void sendReportReminder() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        List<Station> stations = stationRepository.findByReportNoticeTime(now);
        if (stations.isEmpty()) return;

        for (Station station : stations) {
            List<User> members = userStationRepository.findByStationId(station.getId())
                    .stream()
                    .map(us -> us.getUser())
                    .toList();

            for (User member : members) {
                fcmService.send(member, NotificationType.REPORT_REMINDER, station.getId(), station.getPublicId().toString(), station.getName());
            }

            log.info("리포트 알림 발송: 정거장={}, 멤버 수={}", station.getName(), members.size());
        }
    }
}
