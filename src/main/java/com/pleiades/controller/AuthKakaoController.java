package com.pleiades.controller;

import com.pleiades.dto.kakao.KakaoAccountDto;
import com.pleiades.dto.kakao.KakaoTokenDto;
import com.pleiades.dto.kakao.KakaoUserDto;
import com.pleiades.entity.KakaoToken;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.KakaoTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.auth.AuthService;
import com.pleiades.util.JwtUtil;
import com.pleiades.service.auth.KakaoRequest;
import com.pleiades.service.auth.KakaoTokenService;
import com.pleiades.strings.JwtRole;
import com.pleiades.strings.KakaoUrl;
import com.pleiades.util.LocalDateTimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;

@Tag(name = "Auth: Kakao", description = "카카오 인증 관련 API")
@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/auth/login/kakao")
public class AuthKakaoController {

    private final UserRepository userRepository;
    private final KakaoTokenRepository kakaoTokenRepository;
    private final KakaoTokenService kakaoTokenService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Value("${KAKAO_CLIENT_ID}")
    private String KAKAO_CLIENT_ID;

    @Value("${FRONT_ORIGIN}")
    private String FRONT_ORIGIN;

    @Value("${SERVER_DOMAIN}")
    private String SERVER_DOMAIN;

    // 모든 jwt 토큰 만료 or 최초 로그인
    @Operation(summary = "", description = "")
    @GetMapping("")
    public ResponseEntity<Map<String, String>> loginRedirect() {
        try {
            String redirectUrl = KakaoUrl.AUTH_URL.getUrl() +
                    "?response_type=code" +
                    "&client_id=" + KAKAO_CLIENT_ID +
                    "&redirect_uri=" + KakaoUrl.REDIRECT_URI.getRedirectUri(SERVER_DOMAIN);

            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        } catch (Exception e) {
            log.info("kakao login fail: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @Operation(summary = "", description = "")
    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> getAccessToken(@RequestParam("code") String code) {
        try {
            KakaoTokenDto responseToken = KakaoRequest.postAccessToken(code);

            if (responseToken == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();       // 400
            }
            String email = getKakaoEmail(responseToken.getAccessToken());
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();      // 401
            }

            Optional<KakaoToken> oldToken = kakaoTokenRepository.findByEmail(email);
            if (oldToken.isPresent()) {
                oldToken.get().setRefreshToken(responseToken.getRefreshToken());
                oldToken.get().setLastUpdated(Timestamp.from(Instant.from(LocalDateTimeUtil.now())));
                kakaoTokenRepository.save(oldToken.get());
            } else {
                // 카카오 토큰 저장 - 회원가입 완료 후 유저 아이디 추가 저장
                KakaoToken token = new KakaoToken();
                token.setEmail(email);
                token.setRefreshToken(responseToken.getRefreshToken());
                token.setLastUpdated(Timestamp.from(Instant.from(LocalDateTimeUtil.now())));

                User user = userRepository.findByEmail(email).orElse(null);
                token.setUser(user);

                kakaoTokenRepository.save(token);
            }
            kakaoTokenRepository.flush();

            if (kakaoTokenRepository.findByEmail(email).isEmpty()) {
                throw new CustomException(ErrorCode.DB_ERROR);
            }

            log.info("redirect to front/kakaologin");
            // 요청이 없는데 응답 본문을 보낼 순 없음 - 프론트에서 다시 요청하면 이메일로 만든 jwt access, refresh 토큰 전달
            return ResponseEntity
                    .status(HttpStatus.FOUND)       // 302
                    .header("Location", FRONT_ORIGIN+"/kakaologin?hash="+email) // 프론트.com/kakaologin
                    .build();
        } catch (Exception e) {
            log.error("Error in getAccess: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "", description = "")
    @GetMapping("/temp")
    public ResponseEntity<Map<String, String>> responseToken(@RequestParam("hash") String email, HttpServletRequest request) {
        Map<String, String> body = new HashMap<>();

        Optional<KakaoToken> kakaoToken = kakaoTokenRepository.findByEmail(email);
        if (kakaoToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();      // 로그인 필요
        }

        String accessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            user.get().setRefreshToken(refreshToken);
            userRepository.save(user.get());
        }

        body.put("accessToken", accessToken);
        Cookie cookie = authService.setRefreshToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, String.format(
                "%s=%s; Path=%s; HttpOnly; Max-Age=%d; %sSameSite=None",
                cookie.getName(),
                cookie.getValue(),
                cookie.getPath(),
                cookie.getMaxAge(),
                cookie.getSecure() ? "Secure; " : ""
        ));
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.info("headers: " + headers);
        log.info("cookie: " + cookie);
        log.info("body: " + body.get("accessToken"));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(body);   // 200
    }

    private String getKakaoEmail(String token) {
        KakaoUserDto responseUser = KakaoRequest.postUserInfo(token);;
        KakaoAccountDto account = null;
        String email = null;

        if (responseUser != null) { account = responseUser.getKakaoAccount(); log.info("responseUser"); }
        if (account != null) { email = account.getEmail(); log.info("account"); }

        return email;
    }
}