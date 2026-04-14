# 예외 처리 규칙

## 구조

```
exception/
├── CustomException.java       RuntimeException 상속, ErrorCode 보유
├── ErrorCode.java             HTTP 상태 + 메시지를 묶은 enum
├── ErrorResponse.java         { "message": "..." } 응답 DTO
└── GlobalExceptionHandler.java @RestControllerAdvice
```

## 사용 방법

```java
// 예외 발생
throw new CustomException(ErrorCode.USER_NOT_FOUND);

// 새 에러 코드 추가 (ErrorCode.java에 추가)
USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
```

## 주요 ErrorCode

| 코드 | 상태 | 용도 |
|------|------|------|
| `INVALID_TOKEN` | 401 | 토큰 유효하지 않음 |
| `FORBIDDEN_ACCESS` | 403 | 권한 없음 |
| `USER_NOT_FOUND` | 404 | 유저 없음 |
| `CHARACTER_NOT_FOUND` | 404 | 캐릭터 없음 |
| `USER_ALREADY_IN_STATION` | 409 | 이미 스테이션 참여 중 |
| `STATION_FULL` | 409 | 스테이션 정원 초과 |
| `INSUFFICIENT_FUNDS` | 422 | 잔액 부족 |
| `DB_ERROR` | 500 | DB 오류 |

## 규칙

- 비즈니스 예외는 반드시 `CustomException` + `ErrorCode` 조합으로 처리
- `GlobalExceptionHandler`가 모든 `CustomException`을 잡아 `ErrorResponse`로 변환
- 새 에러 케이스 추가 시 `ErrorCode`에만 추가하면 자동으로 처리됨
