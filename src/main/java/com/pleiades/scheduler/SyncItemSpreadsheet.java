//package com.pleiades.scheduler;
//
//import com.pleiades.service.SheetSyncService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Component
//public class SyncItemSpreadsheet {
//    private final SheetSyncService sheetSyncService;
//
//    @Scheduled(cron = "0 */5 * * * *") // 5분마다 실행
//    public void syncFromGoogleSheet() {
//        sheetSyncService.sync();
//    }
//}
