package com.pleiades.controller;

import com.pleiades.dto.ProfileDto;
import com.pleiades.dto.SignUpDto;
import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterImageDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.entity.*;
import com.pleiades.entity.Characters;
import com.pleiades.entity.face.Expression;
import com.pleiades.entity.face.Hair;
import com.pleiades.entity.face.Skin;
import com.pleiades.entity.item.Item;
import com.pleiades.entity.outfit.Bottom;
import com.pleiades.entity.outfit.Shoes;
import com.pleiades.entity.outfit.Top;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.repository.face.ExpressionRepository;
import com.pleiades.repository.face.HairRepository;
import com.pleiades.repository.face.SkinRepository;
import com.pleiades.repository.item.ItemRepository;
import com.pleiades.repository.outfit.BottomRepository;
import com.pleiades.repository.outfit.ShoesRepository;
import com.pleiades.repository.outfit.TopRepository;
import com.pleiades.service.AuthService;
import com.pleiades.service.ImageJsonCreator;
import com.pleiades.strings.TokenStatus;
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
import java.util.*;

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

    private final ImageJsonCreator imageJsonCreator;

    @Autowired
    AuthHomeController(ImageJsonCreator imageJsonCreator)
    {
        this.imageJsonCreator = imageJsonCreator;
    }
    // 첫 접속 화면
    // todo: user 존재하는지 확인 필요
    @PostMapping("")
    public ResponseEntity<Map<String, String>> login(HttpServletRequest request) throws IOException {
        String jwtAccessToken = request.getHeader("accessToken");
        Claims token = jwtUtil.validateToken(jwtAccessToken);

        // access token 유효한 경우
        if (token != null) {
            log.info("로그인: 앱 Access token 유효");
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        String jwtRefreshToken = request.getHeader("refreshToken");
        if(jwtRefreshToken == null) {
            // 프론트한테 refresh token 요청
            return ResponseEntity
                    .status(HttpStatus.PRECONDITION_REQUIRED) // 428
                    .body(Map.of("error", "Refresh Token is required"));
        }
//        else{
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
//        }
    }

    // todo : 얼굴 / 의상 / 아이템 tab 각각 이미지
    // todo: token 검사 필요
    @GetMapping("/signup")
    public ResponseEntity<Map<String, Object>> signupPage(HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        ResponseEntity responseEntity = AuthService.responseTokenStatus(accessToken);
        if (responseEntity != null) { return responseEntity; }

        log.info("signup");
        Map<String, Object> body = new HashMap<>();

        // 캐릭터 이미지 전송
        CharacterFaceDto characterFaceDto = imageJsonCreator.makeCharacterFaceJson();
        CharacterItemDto characterItemDto = imageJsonCreator.makeCharacterItemJson();
        CharacterOutfitDto characterOutfitDto = imageJsonCreator.makeCharacterOutfitJson();

        body.put("face", characterFaceDto);
        body.put("item", characterItemDto);
        body.put("outfit", characterOutfitDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
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

    // todo: id 중복 체크, 별 배경 선택 추가, 캐릭터 & 별 연결
    // todo: 앱 token 프론트와 통신 기능 -> 메소드 따로 추출
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignUpDto signUpDto, HttpServletRequest request) {
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
//          if (session.getAttribute("kakaoRefreshToken") != null) {
//              KakaoToken kakaoToken = new KakaoToken();
//              kakaoToken.setUser(user);
//              kakaoToken.setRefreshToken(session.getAttribute("kakaoRefreshToken").toString());
//              kakaoTokenRepository.save(kakaoToken);
//              session.removeAttribute("kakaoRefreshToken");
//          }
        }

        // access token이 유효하지 않은 경우
        else {

            String jwtRefreshToken = request.getHeader("refreshToken");
            if (jwtRefreshToken == null) {
                // 프론트한테 refresh token 요청
                return ResponseEntity
                        .status(HttpStatus.PRECONDITION_REQUIRED) // 428
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
                            .status(HttpStatus.UNAUTHORIZED) // 401
                            .body(Map.of("accessToken", jwtAccessToken, "refreshToken", jwtRefreshToken));
                }
            }
        }

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

    @PostMapping("/profile")
    public void profile(@RequestBody ProfileDto profileDto, HttpServletResponse response) {
        Optional<User> user = userRepository.findById(profileDto.getUserId());
        if (user.isPresent()) {
            user.get().setProfileUrl(profileDto.getProfileUrl());
            response.setStatus(HttpStatus.CREATED.value());
        }
        response.setStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
    }
}