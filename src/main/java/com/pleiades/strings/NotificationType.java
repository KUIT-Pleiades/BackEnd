package com.pleiades.strings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    FRIEND_REQUEST  ("친구 신청",   "%s님이 친구 신청을 보냈습니다."),
    FRIEND_ACCEPT   ("친구 수락",   "%s님이 친구 신청을 수락했습니다."),
    SIGNAL          ("시그널",      "%s님이 시그널을 보냈습니다."),
    ITEM_SOLD       ("판매 완료",   "내 매물이 판매되었습니다."),
    REPORT_REMINDER ("리포트 알림", "%s 정거장 리포트를 작성할 시간입니다."),
    STATION_JOIN    ("새 멤버",     "%s님이 정거장에 합류했습니다."),
    NOTICE          ("공지사항",    "%s");

    private final String title;
    private final String bodyTemplate;

    public String formatBody(Object... args) {
        return String.format(bodyTemplate, args);
    }
}
