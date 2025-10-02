package com.pleiades.scheduler;

import com.pleiades.service.SheetSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SyncItemSpreadsheet {
    private final SheetSyncService sheetSyncService;

//    @Scheduled(cron = "0 */5 * * * *") // 5분마다 실행 - 로그 보기 힘들어서 임의로 바꿨습니다
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")   // 새벽 3시마다 실행
    public void syncFromGoogleSheet() {
        sheetSyncService.sync();
    }
}
