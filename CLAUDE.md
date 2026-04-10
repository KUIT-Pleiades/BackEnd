# Pleiades Backend

Spring Boot 3.4.1 / Java 17 / MySQL / Redis 기반 백엔드 서버.

## 기술 스택

- **Framework:** Spring Boot 3.4.1
- **Language:** Java 17
- **DB:** MySQL (JPA + Flyway 마이그레이션)
- **Cache:** Redis
- **Auth:** JWT (Access: 1시간, Refresh: 1주 / Cookie)
- **Query:** Spring Data JPA + QueryDSL
- **Push:** Firebase Admin SDK (FCM)
- **Docs:** SpringDoc OpenAPI (Swagger UI `/swagger-ui.html`)
- **Social:** Kakao / Naver OAuth2

## 패키지 구조

```
com.pleiades
├── annotations/    커스텀 Swagger 응답 어노테이션
├── config/         설정 클래스
├── controller/     REST 컨트롤러
├── dto/            Request / Response DTO
├── entity/         JPA 엔티티
├── exception/      CustomException, ErrorCode, GlobalExceptionHandler
├── interceptor/    AuthInterceptor, StationAuthInterceptor
├── repository/     Spring Data JPA Repository
├── scheduler/      스케줄링 작업
├── service/        비즈니스 로직
├── strings/        상수 정의
└── util/           JwtUtil, HeaderUtil 등
```

## 상세 규칙

- @rules/api.md — API 설계 및 인증 규칙
- @rules/entity.md — 엔티티 설계 규칙
- @rules/exception.md — 예외 처리 규칙
