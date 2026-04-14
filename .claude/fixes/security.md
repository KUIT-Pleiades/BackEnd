# 보안 취약점 목록

## 심각도 매우 높음

### [AUTH-1] /auth/login/kakao/temp — 이메일만 알면 누구든 토큰 발급 가능
- **파일:** `AuthKakaoController.java:135`
- **문제:** `GET /auth/login/kakao/temp?hash={email}` 호출 시 이메일 존재 여부만 확인 후 JWT 발급. 상대방 이메일만 알면 그 사람의 액세스 토큰 획득 가능
- **수정 방향:** OAuth 콜백 시 서버에서 단기 임시 코드(UUID)를 발급하고 이를 Redis에 저장. 프론트는 이메일 대신 임시 코드로 토큰 요청, 서버는 코드 검증 후 1회성 삭제

### [AUTH-2] 이메일이 URL에 노출
- **파일:** `AuthKakaoController.java:125`
- **문제:** 콜백 후 프론트로 리다이렉트 시 이메일을 쿼리 파라미터(`?hash=email`)로 전달 → 브라우저 히스토리, 서버 로그, 프록시에 평문 노출
- **수정 방향:** [AUTH-1] 수정과 함께 이메일 대신 임시 코드로 대체

## 심각도 높음

### [AUTH-3] 관리자 인증 없음
- **파일:** `AuthInterceptor.java:46`
- **문제:** `Authorization: admin {email}` 헤더만 보내면 검증 없이 관리자로 인증됨
- **수정 방향:** 관리자 전용 JWT 발급 또는 IP 화이트리스트 적용. 공지사항 API(Step 8) 구현 전 반드시 수정 필요

## 심각도 중간

### [AUTH-4] JWT 서명 방식 deprecated
- **파일:** `JwtUtil.java:29`
- **문제:** `signWith(SignatureAlgorithm.HS256, secretKey)` — deprecated API. secretKey가 짧으면 브루트포스 취약
- **수정 방향:** `Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))` 방식으로 변경

### [AUTH-5] Refresh Token 평문 저장
- **파일:** `User.java` (`refreshToken` 필드)
- **문제:** DB 탈취 시 모든 유저 세션 탈취 가능
- **수정 방향:** 해시 저장 (BCrypt 등) 권장. 단, 실무에서 흔히 타협하는 부분

### [AUTH-6] OAuth state 파라미터 없음
- **파일:** `AuthKakaoController.java:67`
- **문제:** CSRF 방어용 state 파라미터 없음. 콜백 요청이 서버가 시작한 흐름인지 검증 불가
- **수정 방향:** 로그인 요청 시 state(UUID) 생성 → Redis 저장 → 콜백에서 검증

## 심각도 낮음

### [AUTH-7] 로그에 민감 정보 출력
- **파일:** `AuthInterceptor.java:54`, `AuthKakaoController.java:168`, `AuthService.java`
- **문제:** 액세스 토큰, 리프레시 토큰이 로그에 평문 출력
- **수정 방향:** 토큰 로깅 제거 또는 앞 10자리만 출력

### [AUTH-8] String 참조 비교
- **파일:** `KakaoTokenService.java:22`
- **문제:** `userId == foundUserId` — 참조 비교로 항상 false 반환
- **수정 방향:** `userId.equals(foundUserId)` 로 변경
