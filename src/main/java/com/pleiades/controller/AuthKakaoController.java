package com.pleiades.controller;

import com.pleiades.dto.kakao.KakaoAccountDto;
import com.pleiades.dto.kakao.KakaoTokenDto;
import com.pleiades.dto.kakao.KakaoUserDto;
import com.pleiades.entity.KakaoToken;
import com.pleiades.entity.User;
import com.pleiades.repository.KakaoTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.util.JwtUtil;
import com.pleiades.service.KakaoRequest;
import com.pleiades.service.KakaoTokenService;
import com.pleiades.strings.JwtRole;
import com.pleiades.strings.KakaoUrl;
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
    @Autowired
    UserRepository userRepository;

    @Autowired
    KakaoTokenRepository kakaoTokenRepository;

    @Autowired
    KakaoTokenService kakaoTokenService;

    @Autowired
    JwtUtil jwtUtil;

    @Value("${KAKAO_CLIENT_ID}")
    private String KAKAO_CLIENT_ID;

    @Value("${FRONT_ORIGIN}")
    private String FRONT_ORIGIN;

    // 모든 jwt 토큰 만료 or 최초 로그인
    @GetMapping("")
    public ResponseEntity<Map<String, String>> loginRedirect(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
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
    public ResponseEntity<Map<String, String>> getAccessToken(@RequestParam("code") String code, HttpSession session) throws SQLException, IOException {
        log.info("kakao code redirected");
        try {
            HttpHeaders headers = new HttpHeaders();
            Map<String, String> body = new HashMap<>();

            KakaoTokenDto responseToken = KakaoRequest.postAccessToken(code);

            String email = null;

            if (responseToken == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
            email = getKakaoEmail(responseToken.getAccessToken());
            if (email == null) {
                body.put("error", "No email found");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(body);
            }

            Optional<User> user = userRepository.findByEmail(email);

            // refresh token이 만료된 기존 사용자
            if (user.isPresent()) {
                KakaoToken token = new KakaoToken();
                token.setUser(user.get()); token.setRefreshToken(responseToken.getRefreshToken());
                kakaoTokenRepository.save(token);

                String jwtAccessToken = jwtUtil.generateAccessToken(user.get().getId(), JwtRole.ROLE_USER.getRole());
                String jwtRefreshToken = jwtUtil.generateAccessToken(user.get().getId(), JwtRole.ROLE_USER.getRole());

                headers.setLocation(URI.create("/star?userId=" + user.get().getId()));
                body.put("Authorization", responseToken.getAccessToken());
                body.put("AccessToken", jwtAccessToken);
                body.put("RefreshToken", jwtRefreshToken);

                // todo: 얘도 body 전달 못 함
                return ResponseEntity
                        .status(HttpStatus.FOUND) // 302 Found (리다이렉트 상태 코드)
                        .header(headers.toString())
                        .body(body);
            }

            // 새 사용자 - 회원가입 필요

            // 기존 카카오 토큰 삭제
            Optional<KakaoToken> oldToken = kakaoTokenRepository.findByEmail(email);
            if (oldToken.isPresent()) {
                kakaoTokenRepository.delete(oldToken.get());
            }

            // 카카오 토큰 저장 - 회원가입 완료 후 유저 아이디 추가 저장
            KakaoToken token = new KakaoToken();
            token.setEmail(email);
            token.setRefreshToken(responseToken.getRefreshToken());
            kakaoTokenRepository.save(token);

//            session.setAttribute("kakaoAccessToken", responseToken.getAccessToken()); - 소셜 액세스 토큰은 일화용인 걸루,,?

            // session으로 해도 될까
            String signUpAccessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
            String signUpRefreshToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
            session.setAttribute("SignUpAccessToken", signUpAccessToken);
            session.setAttribute("SignUpRefreshToken", signUpRefreshToken);

            log.info("redirect to front/kakaologin");
            // 요청이 없는데 응답 본문을 보낼 순 없음 - 프론트에서 다시 요청하면 이메일로 만든 jwt access, refresh 토큰 전달
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", FRONT_ORIGIN+"/kakaologin") // 프론트.com/kakaologin
                    .build();
        } catch (Exception e) {
            log.error("Error in getAccess: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, String>> reponseToken(HttpSession session) {
        Map<String, String> body = new HashMap<>();
        String accessToken = (String) session.getAttribute("SignUpAccessToken");
        String refreshToken = (String) session.getAttribute("SignUpRefreshToken");

        if (accessToken != null && refreshToken != null) {
            body.put("accessToken", accessToken);
            body.put("refreshToken", refreshToken);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(body);
        }

        // todo: redirect를 시켜줄지 메시지만 전달하고 알아서 리다이렉트하라 할지
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", "/auth/login")
                .build();
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