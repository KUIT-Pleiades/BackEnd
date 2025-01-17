package com.pleiades.controller;

import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.dto.SignUpDto;
import com.pleiades.entity.*;
import com.pleiades.entity.Characters;
import com.pleiades.repository.*;
import com.pleiades.service.JwtUtil;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    // 첫 접속 화면
    @PostMapping("")
    public ResponseEntity<Map<String, String>> login(HttpServletRequest request) throws IOException {
        Map<String, String> body = new HashMap<>();

        String accessToken = request.getParameter("AccessToken");
        String refreshToken = request.getParameter("RefreshToken");
        if (accessToken == null) { return checkRefreshToken(refreshToken, body); }

        Claims token = jwtUtil.validateToken(accessToken);
        if (token == null) { return checkRefreshToken(refreshToken, body); }
        String userId = token.getId();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Location", "/star?userId="+userId)
                .body(body);
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
    public ResponseEntity<Map<String, String>> signUp(@RequestBody SignUpDto signUpDto, HttpSession session) {
        User user = new User();
        user.setSignUp(signUpDto);
        user.setEmail(session.getAttribute("email").toString());
        userRepository.save(user);
        session.removeAttribute("email");

        if (session.getAttribute("kakaoRefreshToken") != null) {
            KakaoToken kakaoToken = new KakaoToken();
            kakaoToken.setUser(user);
            kakaoToken.setRefreshToken(session.getAttribute("kakaoRefreshToken").toString());
            kakaoTokenRepository.save(kakaoToken);
            session.removeAttribute("kakaoRefreshToken");
        }

        if (session.getAttribute("naverRefreshToken") != null) {
            NaverToken naverToken = new NaverToken();
            naverToken.setUser(user);
            naverToken.setRefreshToken(session.getAttribute("naverRefreshToken").toString());
            naverToken.setAccessToken(session.getAttribute("naverAccessToken").toString());
            naverToken.setLastUpdated(System.currentTimeMillis());
            naverTokenRepository.save(naverToken);
            session.removeAttribute("naverRefreshToken");
            session.removeAttribute("naverAccessToken");
        }

        Star star = new Star();
        star.setUserId(signUpDto.getId());
        star.setBackgroundId(signUpDto.getBackgroundId());
        starRepository.save(star);

        CharacterFaceDto faceDto = signUpDto.getFace();
        Face face = new Face();
        face.setFace(faceDto);

        CharacterOutfitDto outfitDto = signUpDto.getOutfit();
        Outfit outfit = new Outfit();
        outfit.setOutfit(outfitDto);

        CharacterItemDto itemDto = signUpDto.getItem();
        Item item = new Item();
//        item.setId(itemDto.get);
//        item.setItem(itemDto);

        Characters character = new Characters();
        character.setUser(user);
        character.setFace(face);        // 얼굴을 얼굴 테이블을 만들어서 저장할지, 머리, 표정, 피부색 따로 저장할지 정해야되는데 얼굴 테이블을 만드는 건 불필요해 보임
        character.setOutfit(outfit);    // 옷도 사실 마찬가지
        character.setItem(item);        // 얜 뭐 모르겠다
        characterRepository.save(character);

        Map<String, String> body = new HashMap<>();

        String jwtAccessToken = jwtUtil.generateAccessToken(user.getId(), JwtRole.ROLE_USER.getRole());
        String jwtRefreshToken = jwtUtil.generateAccessToken(user.getId(), JwtRole.ROLE_USER.getRole());

        body.put("AccessToken", jwtAccessToken);
        body.put("RefreshToken", jwtRefreshToken);

        log.info("(b) Access token: " + jwtAccessToken);
        log.info("(b) Refresh token: " + jwtRefreshToken);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", "/star?userId"+signUpDto.getId())
                .body(body);
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