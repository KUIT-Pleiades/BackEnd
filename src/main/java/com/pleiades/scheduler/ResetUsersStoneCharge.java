package com.pleiades.scheduler;

import com.pleiades.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ResetUsersStoneCharge {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul") // 매일 자정(00:00:00)에 실행
    public void resetStoneCharge() {
        userRepository.resetStoneChargeToFalse();
    }
}
