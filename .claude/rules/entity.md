# 엔티티 설계 규칙

## 핵심 엔티티 관계

```
User (1) ─── (1) Characters
     (1) ─── (1) Star
     (1) ─── (N) UserStation ─── (N) Station
     (1) ─── (N) Friend (sender / receiver)
     (1) ─── (N) Signal (sender / receiver)
     (1) ─── (N) Ownership ─── (1) TheItem
     (1) ─── (N) Report ─── (1) Question
     (1) ─── (1) KakaoToken
     (1) ─── (1) NaverToken
```

## User 엔티티 주요 필드

`id` (String), `email`, `userName`, `birthDate`, `createdDate`,
`profileUrl`, `characterUrl`, `refreshToken`, `coin`, `stone`, `stoneCharge`

> FCM 토큰은 User에 직접 추가하지 않음 — 멀티 기기 지원을 위해 별도 `FcmToken` 엔티티 사용

## 네이밍 규칙

- **클래스명:** 단수 (예: `FcmToken`, `Notification`)
- **테이블명:** 복수, `@Table(name = "fcm_tokens")` 명시

## 엔티티 작성 규칙

- PK: `@GeneratedValue(strategy = GenerationType.IDENTITY)` + `Long id` (User만 `String id` 예외)
- Enum 필드: `@Enumerated(EnumType.STRING)` 사용 (ORDINAL 금지)
- Fetch: 연관관계는 기본 `FetchType.LAZY`
- 복합 PK: `@EmbeddedId` + `@Embeddable` (예: `UserStationId`)
- 낙관적 락 필요 시: `@Version Long version` (예: `Station`)
- UUID 필드: `BINARY(16)` 컬럼 타입 사용

## DB 마이그레이션

- **Flyway** 사용. 엔티티 변경 시 반드시 `src/main/resources/db/migration/` 에 SQL 파일 추가
- `spring.jpa.hibernate.ddl-auto=validate` — Hibernate가 스키마를 자동 변경하지 않음

## DTO 변환

- Entity ↔ DTO 변환에 **ModelMapper** 사용
- DTO는 `dto/` 패키지에 기능별로 분류
