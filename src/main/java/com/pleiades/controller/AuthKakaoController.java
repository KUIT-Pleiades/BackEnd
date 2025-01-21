package com.pleiades.controller;

import com.pleiades.dto.kakao.KakaoAccountDto;
import com.pleiades.dto.kakao.KakaoTokenDto;
import com.pleiades.dto.kakao.KakaoUserDto;
import com.pleiades.entity.KakaoToken;
import com.pleiades.entity.User;
import com.pleiades.repository.KakaoTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.service.JwtUtil;
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
        }
        catch (Exception e) {
            log.info("kakao login fail"+e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .build();
        }
    }

    // 인가 코드 재발급은 불가피한가? 그럼 소셜 토큰을 쓰는 이유가 있나?
    // 여기서 소셜 토큰 확인 해야함 - 아닌 듯?
    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> getAccessToken(@RequestParam("code") String code, HttpSession session) throws SQLException, IOException {
        log.info("kakao code redirected");
        try {
            HttpHeaders headers = new HttpHeaders();
            Map<String, String> body = new HashMap<>();

            KakaoTokenDto responseToken = KakaoRequest.postAccessToken(code);
            log.info("Access token: " + responseToken.getAccessToken());
            String email = null;

            if (responseToken != null) { email = getKakaoEmail(responseToken.getAccessToken()); }

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

                log.info("(a) Access token: " + jwtAccessToken);
                log.info("(a) Refresh token: " + jwtRefreshToken);

                return ResponseEntity
                        .status(HttpStatus.FOUND) // 302 Found (리다이렉트 상태 코드)
                        .header(headers.toString())
                        .body(body);
            }

            log.info("access token", responseToken.getAccessToken());

            // 회원가입 완료 후 저장될 회원 정보
            session.setAttribute("kakaoAccessToken", responseToken.getAccessToken());
            session.setAttribute("kakaoRefreshToken", responseToken.getRefreshToken());
            session.setAttribute("email", responseToken.getAccessToken());


            // 요청이 없는데 응답 본문을 보낼 순 없음
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", "/auth/signup") // 프론트.com/kakaologin
                    .build();
        } catch (Exception e) {
            log.error("Error in getAccess: " + e.getMessage());
        }
        return null;
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