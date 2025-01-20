package com.pleiades.controller;

import com.pleiades.dto.SignUpDto;
import com.pleiades.entity.*;
import com.pleiades.entity.Characters;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.util.JwtUtil;
import com.pleiades.strings.JwtRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.pleiades.exception.ErrorCode.INVALID_USER_EMAIL;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthHomeController {
    private final JwtUtil jwtUtil = new JwtUtil();

    @Autowired
    UserRepository userRepository;

    @Autowired
    KakaoTokenRepository kakaoTokenRepository;

    @Autowired
    NaverTokenRepository naverTokenRepository;

    @Autowired
    private StarRepository starRepository;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private FaceRepository faceRepository;

    // 첫 접속 화면
    @PostMapping("")
    public ResponseEntity<Map<String, String>> login(HttpServletRequest request) throws IOException {

        String jwtAccessToken = request.getHeader("accessToken");
        Claims token = jwtUtil.validateToken(jwtAccessToken);

        // access token 유효한 경우
        if (token != null) {
            log.info("로그인: 앱 Access token 유효 - " + jwtAccessToken);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        String jwtRefreshToken = request.getHeader("refreshToken");
        if(jwtRefreshToken == null) {
            // 프론트한테 refresh token 요청
            return ResponseEntity
                    .status(HttpStatus.PRECONDITION_REQUIRED) // 428
                    .body(Map.of("error", "Refresh Token is required"));
        }
        else{
            Claims refreshToken = jwtUtil.validateToken(jwtRefreshToken);
            // 프론트한테 소셜 로그인 재요청
            if (refreshToken == null) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN) // 403
                        .body(Map.of("error", "Social login is required"));
            }

            // refresh token은 유효한 경우
            else{
                log.info("로그인: 앱 Refresh token만 유효 - " + jwtRefreshToken);
                String email = refreshToken.getSubject();
                // 새로 jwt 토큰들 생성 -> 프론트한테 넘겨줌
                jwtAccessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
                jwtRefreshToken = jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED) // 401
                        .body(Map.of("accessToken", jwtAccessToken, "refreshToken", jwtRefreshToken));
            }
        }
//        Map<String, String> body = new HashMap<>();
//
//        String accessToken = request.getHeader("AccessToken");
//        String refreshToken = request.getHeader("RefreshToken");
//        if (accessToken == null) { return checkRefreshToken(refreshToken, body); }
//
//        Claims token = jwtUtil.validateToken(accessToken);
//        if (token == null) {return checkRefreshToken(refreshToken, body); }
//        String userId = token.getId();
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .header("Location", "/star?userId="+userId)
//                .body(body);
    }

    // 소셜 로그인 페이지
    @GetMapping("/login")
    public void socialLogin(HttpServletResponse response) {

    }

    @GetMapping("/signup")
    public void setCharacter(HttpServletRequest request, HttpServletResponse response) {
        log.info("signup");

        // 캐릭터 이미지 전송

        return ;
    }

    @GetMapping("/checkId")
    public ResponseEntity<Map<String, String>> checkId(HttpServletRequest request) {
        String id = request.getParameter("id");
        Map<String, String> body = new HashMap<>();

        if (id == null) {
            body.put("available", "false");
            body.put("message", "Username is required.");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            body.put("available", "false");
            body.put("message", "The username is already taken.");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(body);
        }

        body.put("available", "true");
        body.put("message", "The username is available.");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody SignUpDto signUpDto,
                                                      HttpServletRequest request) {
        User user = new User();
        user.setSignUp(signUpDto); // id, nickname, birthDate, face, outfit, item

        String jwtAccessToken = request.getHeader("accessToken");
        Claims token = jwtUtil.validateToken(jwtAccessToken);

        // access token 유효한 경우 -> naver / kakao 랑 user 매핑
        if (token != null) {
            log.info("회원가입: Access token 유효 - " + jwtAccessToken);

            String email = token.getSubject();   // email은 token의 subject에 저장되어 있음!

            // email - naver
            if(email.contains("@naver.com")) {
                user.setEmail(email);
                NaverToken naverToken = naverTokenRepository.findByEmail(email).orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_USER_EMAIL)
                );
                naverToken.setUser(user);
                user.setNaverToken(naverToken);

                userRepository.save(user);
                naverTokenRepository.save(naverToken);
            }
            // todo : email - kakao
        }

        // access token이 유효하지 않은 경우
        else {

            String jwtRefreshToken = request.getHeader("refreshToken");
            if (jwtRefreshToken == null) {
                // 프론트한테 refresh token 요청
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED) // 401
                        .body(Map.of("error", "Refresh Token is required"));
            } else {
                Claims refreshToken = jwtUtil.validateToken(jwtRefreshToken);
                // 프론트한테 소셜 로그인 재요청
                if (refreshToken == null) {
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN) // 403
                            .body(Map.of("error", "Social login is required"));
                }

                // refresh token은 유효한 경우
                else {
                    log.info("회원가입: Refresh token 유효 - " + jwtRefreshToken);
                    String email = refreshToken.getSubject();
                    // 새로 jwt 토큰들 생성 -> 프론트한테 넘겨줌
                    jwtAccessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
                    jwtRefreshToken = jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());

                    return ResponseEntity
                            .status(HttpStatus.OK) // 200
                            .body(Map.of("accessToken", jwtAccessToken, "refreshToken", jwtRefreshToken));
                }
            }
        }
//        if (session.getAttribute("kakaoRefreshToken") != null) {
//            KakaoToken kakaoToken = new KakaoToken();
//            kakaoToken.setUser(user);
//            kakaoToken.setRefreshToken(session.getAttribute("kakaoRefreshToken").toString());
//            kakaoTokenRepository.save(kakaoToken);
//            session.removeAttribute("kakaoRefreshToken");
//        }
//
//        Star star = new Star();
//        star.setUserId(signUpDto.getId());
//        // star.setBackgroundId(signUpDto.getBackgroundId());
//        starRepository.save(star);
//
//        log.info("star saved");

        // todo: 윤희's 할 일

//        Characters character = new Characters();
//        character.setUser(user);
//        character.setFace(face);
//        character.setOutfit(outfit);
//        character.setItem(item);
//        characterRepository.save(character);

//        log.info("character saved");

        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 : 회원가입 완료
    }

    private ResponseEntity<Map<String, String>> checkRefreshToken(String refreshToken, Map<String, String> body) {
        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", "/auth/login")
                    .build();
        }

        Claims token = jwtUtil.validateToken(refreshToken);
        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", "/auth/login")
                    .build();
        }
        String userId = token.getId();
        String accessToken = jwtUtil.generateAccessToken(userId, JwtRole.ROLE_USER.getRole());

        body.put("AccessToken", accessToken);
        log.info("(c) Access token: " + accessToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Location", "/star?userId="+userId)
                .body(body);
    }
}