package com.pleiades.controller;

import com.pleiades.dto.kakao.KakaoAccountDto;
import com.pleiades.dto.kakao.KakaoTokenDto;
import com.pleiades.dto.kakao.KakaoUserDto;
import com.pleiades.entity.KakaoToken;
import com.pleiades.entity.User;
import com.pleiades.repository.KakaoTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.AuthService;
import com.pleiades.util.HashStringUtil;
import com.pleiades.util.JwtUtil;
import com.pleiades.service.KakaoRequest;
import com.pleiades.service.KakaoTokenService;
import com.pleiades.strings.JwtRole;
import com.pleiades.strings.KakaoUrl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/auth/login/kakao")
public class AuthKakaoController {

    UserRepository userRepository;
    KakaoTokenRepository kakaoTokenRepository;
    KakaoTokenService kakaoTokenService;
    JwtUtil jwtUtil;
    AuthService authService;

    @Value("${KAKAO_CLIENT_ID}")
    private String KAKAO_CLIENT_ID;

    @Value("${FRONT_ORIGIN}")
    private String FRONT_ORIGIN;

    @Autowired
    AuthKakaoController(UserRepository userRepository, KakaoTokenRepository kakaoTokenRepository,
                        KakaoTokenService kakaoTokenService, JwtUtil jwtUtil, AuthService authService) {
        this.userRepository = userRepository;
        this.kakaoTokenRepository = kakaoTokenRepository;
        this.kakaoTokenService = kakaoTokenService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    // 모든 jwt 토큰 만료 or 최초 로그인
    @GetMapping("")
    public ResponseEntity<Map<String, String>> loginRedirect() {
        try {
            log.info("kakao login start");

            String redirectUrl = KakaoUrl.AUTH_URL.getUrl() +
                    "?response_type=code" +
                    "&client_id=" + KAKAO_CLIENT_ID +
                    "&redirect_uri=" + KakaoUrl.REDIRECT_URI.getUrl();

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

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> getAccessToken(@RequestParam("code") String code, HttpSession session) {
        log.info("kakao code redirected");
        try {
            HttpHeaders headers = new HttpHeaders();
            Map<String, String> body = new HashMap<>();

            KakaoTokenDto responseToken = KakaoRequest.postAccessToken(code);

            String email = null;

            if (responseToken == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();       // 400
            }
            email = getKakaoEmail(responseToken.getAccessToken());
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();      // 401
            }

            // 기존 카카오 토큰 삭제
            Optional<KakaoToken> oldToken = kakaoTokenRepository.findByEmail(email);
            oldToken.ifPresent(kakaoToken -> kakaoTokenRepository.delete(kakaoToken));

            // 카카오 토큰 저장 - 회원가입 완료 후 유저 아이디 추가 저장
            KakaoToken token = new KakaoToken();
            token.setEmail(email);
            token.setRefreshToken(responseToken.getRefreshToken());
            kakaoTokenRepository.save(token);

//            session.setAttribute("kakaoAccessToken", responseToken.getAccessToken()); - 소셜 액세스 토큰은 일화용인 걸루,,? 아마두,,,

            // session으로 해도 될까
            String accessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
            String refreshToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
            session.setAttribute("accessToken", accessToken);
            session.setAttribute("refreshToken", refreshToken);

            String hashedEmail = HashStringUtil.hashString(email);

            log.info("redirect to front/kakaologin");
            // 요청이 없는데 응답 본문을 보낼 순 없음 - 프론트에서 다시 요청하면 이메일로 만든 jwt access, refresh 토큰 전달
            return ResponseEntity
                    .status(HttpStatus.FOUND)       // 302
                    .header("Location", FRONT_ORIGIN+"/kakaologin?hash="+hashedEmail) // 프론트.com/kakaologin
                    .build();
        } catch (Exception e) {
            log.error("Error in getAccess: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/temp")
    public ResponseEntity<Map<String, String>> reponseToken(@RequestParam("hash") String hash, HttpSession session) {
        Map<String, String> body = new HashMap<>();
        String accessToken = (String) session.getAttribute("accessToken");
        String refreshToken = (String) session.getAttribute("refreshToken");

        if (accessToken == null || refreshToken == null) {
            log.info("no tokens");
            body.put("error", "No token found - social login required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);   // 401
        }
        Claims access = jwtUtil.validateToken(accessToken);
        String hashedSubject = HashStringUtil.hashString(access.getSubject());
        if (!hash.equals(hashedSubject)) {
            log.info("different email");
            body.put("error", "different email");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);    // 400
        }
        log.info("same email");
        body.put("accessToken", accessToken);
        Cookie cookie = authService.setRefreshToken(refreshToken);

        return ResponseEntity.status(HttpStatus.OK).header("refreshToken", cookie.toString()).body(body);   // 200
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