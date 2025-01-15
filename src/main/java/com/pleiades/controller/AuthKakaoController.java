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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Value("${KAKAO_CLIENT_ID}");
//    String KAKAO_CLIENT_ID;

    @GetMapping("")
    public void loginRedirect(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String userId = request.getParameter("userId");
        if (session != null && userId != null) {
            Object tokenAttribute = session.getAttribute("kakaoAccessToken");
            // 액세스 토큰이 존재
            if (tokenAttribute != null) {
                String accessToken = tokenAttribute.toString();
                // 액세스 토큰이 유효
                if (kakaoTokenService.checkAccessTokenValidation(accessToken, userId)) { response.sendRedirect("/star?userId=" + userId); return; }
                String newAccessToken = kakaoTokenService.checkRefreshTokenValidation(userId);      // 액세스 토큰 만료
                if (newAccessToken != null) { response.sendRedirect("/star?userId=" + userId); return; }    // 리프레시 토큰 유효
            }
        }
        // 기존 사용자라면 리프레시 토큰 삭제
        Optional<KakaoToken> token = kakaoTokenRepository.findByUser_Id(userId);
        token.ifPresent(kakaoToken -> kakaoTokenRepository.delete(kakaoToken));

        // 최초 로그인 or 리프레시 토큰 만료
        String redirectUrl = KakaoUrl.AUTH_URL.getUrl() +
                "?response_type=code" +
                "&client_id=" + KakaoUrl.KAKAO_CLIENT_ID.getUrl() +
                "&redirect_uri=" + KakaoUrl.REDIRECT_URI.getUrl();

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> getAccessToken(@RequestParam("code") String code, HttpSession session) throws SQLException, IOException {
        try {
            HttpHeaders headers = new HttpHeaders();
            Map<String, String> body = new HashMap<>();

            KakaoTokenDto responseToken = KakaoRequest.postAccessToken(code);
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

            // 회원가입 완료 후 저장될 회원 정보
            session.setAttribute("kakaoAccessToken", responseToken.getAccessToken());
            session.setAttribute("kakaoRefreshToken", responseToken.getRefreshToken());
            session.setAttribute("email", responseToken.getAccessToken());

            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", "/auth/signup")
                    .build();
        } catch (Exception e) {
            log.error("Error in getAccess: " + e.getMessage());
        }
        return null;
    }

    private String getKakaoEmail(String token) {
        KakaoUserDto responseUser = null;
        KakaoAccountDto account = null;
        String email = null;

        responseUser = KakaoRequest.postUserEmail(token);

        if (responseUser != null) { account = responseUser.getKakaoAccount(); }
        if (account != null) { email = account.getEmail(); }

        return email;
    }
}