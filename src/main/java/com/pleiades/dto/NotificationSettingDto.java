package com.pleiades.dto;

import com.pleiades.entity.NotificationSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationSettingDto {
    private boolean friendRequestEnabled;
    private boolean signalEnabled;
    private boolean reportReminderEnabled;
    private boolean itemSoldEnabled;
    private boolean stationJoinEnabled;
    private boolean noticeEnabled;

    public NotificationSettingDto(NotificationSetting setting) {
        this.friendRequestEnabled = setting.isFriendRequestEnabled();
        this.signalEnabled = setting.isSignalEnabled();
        this.reportReminderEnabled = setting.isReportReminderEnabled();
        this.itemSoldEnabled = setting.isItemSoldEnabled();
        this.stationJoinEnabled = setting.isStationJoinEnabled();
        this.noticeEnabled = setting.isNoticeEnabled();
    }
}
