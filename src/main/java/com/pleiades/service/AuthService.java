package com.pleiades.service;

import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.entity.Star;
import com.pleiades.entity.StarBackground;
import com.pleiades.entity.User;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.Item.Item;
import com.pleiades.repository.StarBackgroundRepository;
import com.pleiades.repository.StarRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.strings.JwtRole;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class AuthService {
    UserRepository userRepository;
    StarRepository starRepository;
    StarBackgroundRepository starBackgroundRepository;
    CharacterRepository characterRepository;

    JwtUtil jwtUtil;
    ImageJsonCreator imageJsonCreator;

    @Autowired
    AuthService(UserRepository userRepository, StarRepository starRepository, StarBackgroundRepository starBackgroundRepository,
                   CharacterRepository characterRepository, JwtUtil jwtUtil, ImageJsonCreator imageJsonCreator) {
        this.userRepository = userRepository; this.starRepository = starRepository;
        this.starBackgroundRepository = starBackgroundRepository;
        this.characterRepository = characterRepository;
        this.jwtUtil = jwtUtil; this.imageJsonCreator = imageJsonCreator;
    }


    public ValidationStatus checkToken(String token) {
        log.info("AuthService checkToken");
        if (token == null || token.isEmpty()) { log.info("token is empty"); return ValidationStatus.NONE; }

        Claims tokenClaim = jwtUtil.validateToken(token);
        if (tokenClaim == null) { log.info("token is not valid"); return ValidationStatus.NOT_VALID; }

        log.info("token is valid");
        return ValidationStatus.VALID;
    }

    public ResponseEntity<Map<String, String>> responseRefreshTokenStatus(String refreshToken) {
        log.info("AuthService responseRefreshTokenStatus");

        Map<String, String> body = new HashMap<>();
        ValidationStatus tokenStatus = checkToken(refreshToken);

        if (tokenStatus.equals(ValidationStatus.NONE)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED)
                    .body(Map.of("message","Refresh Token is required"));     // 428
        }

        if (tokenStatus.equals(ValidationStatus.NOT_VALID)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message","Refresh token is not valid. Social login is required"));     // 403
        }

        Claims claims = jwtUtil.validateToken(refreshToken);
        String email = claims.getSubject();

        String newAccessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());

        Cookie cookie = setRefreshToken(newRefreshToken);
        body.put("accessToken", newAccessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, String.format(
                "%s=%s; Path=%s; HttpOnly; Max-Age=%d; %s; SameSite=None",  // Strict -> None
                cookie.getName(),
                cookie.getValue(),
                cookie.getPath(),
                cookie.getMaxAge(),
                cookie.getSecure() ? "Secure" : ""
        ));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity
                .status(HttpStatus.CREATED)     // 201
                .headers(headers)
                .body(body);
    }

    public ValidationStatus userValidation(String accessToken) {
        log.info("AuthService userValidation");

        Claims claims = jwtUtil.validateToken(accessToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            log.info("no user");
            return ValidationStatus.NOT_VALID;
        }

        Optional<Star> star = starRepository.findByUserId(user.get().getId());
        if (star.isEmpty()) {
            log.info("no star");
            return ValidationStatus.NOT_VALID;
        }

        Optional<StarBackground> starBackground = starBackgroundRepository.findById(star.get().getBackground().getId());

        if (starBackground.isEmpty()) {
            log.info("no star background");
            return ValidationStatus.NOT_VALID;
        }

        Optional<Characters> character = characterRepository.findByUser(user.get());

        if (character.isEmpty()) {
            log.info("no character");
            return ValidationStatus.NOT_VALID;
        }

        return ValidationStatus.VALID;
    }

    // access token 유효한 경우에만 사용
    // Todo: 202 회원가입 안했을 시 -> star, starBG, User NOT FOUND
    public ResponseEntity<Map<String, Object>> responseUserInfo(String accessToken) {
        log.info("AuthService responseUserInfo");

        Map<String, Object> body = new HashMap<>();

        if (accessToken == null) { log.info("no access token"); return new ResponseEntity<>(body, HttpStatus.PRECONDITION_REQUIRED);}

        ValidationStatus userValidation = userValidation(accessToken);

        if (userValidation.equals(ValidationStatus.NOT_VALID)) {
            body.put("message", "Need Sign-up");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(body);
        }

        Claims claims = jwtUtil.validateToken(accessToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        Optional<Star> star = starRepository.findByUserId(user.get().getId());
        Optional<StarBackground> starBackground = starBackgroundRepository.findById(star.get().getBackground().getId());
        Optional<Characters> character = characterRepository.findByUser(user.get());

        String profileUrl = user.get().getProfileUrl();
        String characterUrl = user.get().getCharacterUrl();

        body.put("userId", user.get().getId());
        body.put("userName", user.get().getUserName());
        body.put("birthDate", user.get().getBirthDate());
        body.put("starBackground", "background_01");   // starBackground.get().getName()
        body.put("profile", "QmURNcGX98UAecKyEELM39117X7RwQZE8B1dtm56B4vxEJ");    // todo: characterUrl
        body.put("character", "QmWC4899NqLPTqMSVFNZS5qzSUvCH1agcCdRzRrFe1um85");    // todo: profileUrl

        log.info("body: " + body);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    public Cookie setRefreshToken(String refreshToken) {
        log.info("AuthService setRefreshToken");
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(false);      // 임시 false
        cookie.setSecure(false);        // 추후 true로 변경
        cookie.setMaxAge(7 * 24 * 60 * 60);

        return cookie;
    }

    public ResponseEntity<Map<String, String>> responseAccessTokenStatus(String accessToken) {
        ValidationStatus tokenStatus = checkToken(accessToken);

        if (tokenStatus.equals(ValidationStatus.NONE)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();      // 428
        }

        if (tokenStatus.equals(ValidationStatus.NOT_VALID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();      // 401
        }

        Claims claims = jwtUtil.validateToken(accessToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.ACCEPTED).build(); }   // user 없음: 202

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
