# FCM 푸시 알림

## 알림 발생 이벤트

| 타입 | 설명 | 트리거 |
|------|------|--------|
| `FRIEND_REQUEST` | 받은 친구 신청 | 상대방이 친구 신청 |
| `FRIEND_ACCEPT` | 친구의 친구 신청 수락 | 상대방이 수락 |
| `SIGNAL` | 친구가 보낸 시그널 | Signal 생성 |
| `ITEM_SOLD` | 내 매물 판매됨 | ResaleListing 구매 완료 |
| `REPORT_REMINDER` | 정거장 리포트 알림 | Station.reportNoticeTime 기준 스케줄러 |
| `STATION_JOIN` | 정거장 가입자 발생 | UserStation 생성 |
| `NOTICE` | 공지사항 | 관리자가 전체 유저 대상으로 발송 |

## 추가 엔티티

### FcmToken
- User 1:N FcmToken (멀티 기기 지원)
- 필드: `id`, `user`, `token` (unique), `deviceType` (DeviceType enum), `createdAt`, `updatedAt`
- DeviceType enum: `WEB`, `ANDROID`, `IOS` (현재 클라이언트는 PWA이므로 WEB만 사용)
- 로그인 시 token upsert, 로그아웃 시 해당 token 삭제
- 토큰 갱신(`onTokenRefresh`)은 클라이언트가 새 토큰을 서버에 전달 → upsert로 저장 (이전 토큰 별도 삭제 없음)

## 토큰 생명주기 관리

- 토큰 유효성 검사는 서버가 하지 않고 Firebase가 담당
- 알림 발송 후 Firebase로부터 `UNREGISTERED` / `INVALID_ARGUMENT` 응답을 받은 경우 해당 토큰을 DB에서 삭제 (발송 실패 시 자연 정리)
- 죽은 토큰이 일시적으로 DB에 잔존할 수 있으나 알림 발송에는 지장 없음

## 알림 발송 흐름

```
이벤트 발생
    ↓
DB에서 해당 유저의 FcmToken 목록 조회
    ↓
Firebase Admin SDK로 각 토큰에 발송 요청 (sendEachForMulticast)
    ↓
발송 결과 처리
    ├── 성공 → 기기에 알림 도착
    └── 실패 (UNREGISTERED 등) → 해당 토큰 DB에서 삭제
```

### Notification
- 인앱 알림함 + FCM 발송 이력
- 필드: `id`, `receiver` (User), `type` (NotificationType enum), `title`, `body`, `isRead`, `createdAt`, `relatedId` (연관 엔티티 ID, nullable)


### NotificationSetting
- User 1:1 NotificationSetting
- 필드: `id`, `user`, `friendRequestEnabled`, `signalEnabled`, `reportReminderEnabled`, `itemSoldEnabled`, `stationJoinEnabled`, `noticeEnabled`
- 기본값 전부 `true`
- 알림 전체 ON/OFF 기능 존재 (별도 필드 없이 클라이언트에서 관리)


## 알림 메시지 템플릿

`NotificationType` enum에서 title/body 템플릿 관리 (DB 저장 X)

| 타입 | title | body 템플릿 |
|------|-------|------------|
| `FRIEND_REQUEST` | 친구 신청 | `%s님이 친구 신청을 보냈습니다.` |
| `FRIEND_ACCEPT` | 친구 수락 | `%s님이 친구 신청을 수락했습니다.` |
| `SIGNAL` | 시그널 | `%s님이 시그널을 보냈습니다.` |
| `ITEM_SOLD` | 판매 완료 | `내 매물이 판매되었습니다.` |
| `REPORT_REMINDER` | 리포트 알림 | `%s 정거장 리포트를 작성할 시간입니다.` |
| `STATION_JOIN` | 새 멤버 | `%s님이 정거장에 합류했습니다.` |
| `NOTICE` | 공지사항 | `%s` |

호출 방식: `fcmService.send(receiver, NotificationType.SIGNAL, relatedId, sender.getUserName())`

## 작업 목록

- ✅ Step 1. Firebase 초기화 설정 (`FirebaseConfig.java`)
- ✅ Step 2. 엔티티 + Flyway 마이그레이션 (`FcmToken`, `Notification`, `NotificationSetting`, `V4__create_fcm_tables.sql`)
- ✅ Step 3. Repository (`FcmTokenRepository`, `NotificationRepository`, `NotificationSettingRepository`)
- ✅ Step 4. FCM 발송 공통 서비스 (`FcmService.java`, `NotificationType` enum에 템플릿 포함)
- ✅ Step 5. FcmToken 관리 API (`POST /auth/fcm-token` upsert, `POST /auth/logout` 시 토큰 삭제 연동)
- ✅ Step 6. 이벤트별 FCM 발송 연동 (`FriendService`, `SignalService`, `ResaleStoreService`, `UserStationService`)
- ✅ Step 7. 리포트 알림 스케줄러 신규 구현 (`ReportNoticeScheduler.java`, 매분 실행)
- Step 8. 공지사항 API (`/admin/notice`)
- Step 9. NotificationSetting API (조회/수정)