# API 설계 및 인증 규칙

## 컨트롤러 기본 패턴

```java
@RestController
@RequestMapping("/resource")
@Tag(name = "Resource", description = "...")
public class ResourceController {

    @GetMapping("/{id}")
    public ResponseEntity<SomeDto> get(HttpServletRequest request) {
        String email = (String) request.getAttribute("email"); // 인증된 유저
        ...
    }
}
```

## 인증

- JWT Bearer 토큰을 `Authorization` 헤더로 전달
- `AuthInterceptor`가 검증 후 `request.setAttribute("email", email)` 설정
- Refresh Token은 쿠키 `refreshToken`으로 전달
- 인증 불필요 경로: `/auth/refresh`, `/auth/login/**`, `/swagger-ui/**`, `/v3/api-docs/**`

## 응답 형식

공통 래퍼 클래스 없음. 컨트롤러에서 직접 반환.

```java
// 성공
return ResponseEntity.ok(dto);
return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "success"));

// 실패 → CustomException throw (GlobalExceptionHandler가 처리)
throw new CustomException(ErrorCode.USER_NOT_FOUND);
```

## Swagger 문서화

- 컨트롤러에 `@Tag`, 메서드에 `@Operation` 적용
- 공통 에러 응답은 `@UserNotFoundResponse` 같은 커스텀 어노테이션으로 정의 (`annotations/` 패키지)

## CORS

- 프론트엔드 도메인(환경변수), `localhost:5173` 허용
- `credentials: true` (쿠키 전송)
