package com.pleiades.service.auth;

import com.pleiades.dto.UserInfoDto;
import com.pleiades.entity.Star;
import com.pleiades.entity.StarBackground;
import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.entity.character.Characters;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.model.TokenValidateResult;
import com.pleiades.repository.*;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.service.UserService;
import com.pleiades.strings.FriendStatus;
import com.pleiades.strings.JwtRole;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.HeaderUtil;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final StationRepository stationRepository;
    private final UserStationRepository userStationRepository;
    private final FriendRepository friendRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final StarRepository starRepository;
    private final StarBackgroundRepository starBackgroundRepository;
    private final CharacterRepository characterRepository;

    private final JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<Map<String, String>> responseRefreshTokenStatus(String refreshToken) {
        log.info("AuthService responseRefreshTokenStatus");

        Map<String, String> body = new HashMap<>();

        TokenValidateResult tokenStatus = TokenValidateResult.of(refreshToken);

        if (tokenStatus.getValidationStatus() == ValidationStatus.NONE) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED)
                    .body(Map.of("message","Refresh Token is required"));     // 428
        }

        if (tokenStatus.getValidationStatus() == ValidationStatus.NOT_VALID) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message","Refresh token is not valid. Social login is required"));     // 403
        }

        String email = tokenStatus.getEmail();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        String savedRefreshToken = user.get().getRefreshToken();

        if (!refreshToken.equals(savedRefreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message","Refresh token is not valid. Social login is required"));     // 403 - 401로 바꾸고 싶음
        }

        String newAccessToken = jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());


        user.get().setRefreshToken(newRefreshToken);
        userRepository.save(user.get());

        Cookie cookie = setRefreshToken(newRefreshToken);
        body.put("accessToken", newAccessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, String.format(
                "%s=%s; Path=%s; HttpOnly; Max-Age=%d; %sSameSite=Strict",
                cookie.getName(),
                cookie.getValue(),
                cookie.getPath(),
                cookie.getMaxAge(),
                cookie.getSecure() ? "Secure; " : ""
        ));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity
                .status(HttpStatus.CREATED)     // 201
                .headers(headers)
                .body(body);
    }

    // 회원가입 여부
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

    // 회원가입 안 한 경우 -> 202 or 204
    public ResponseEntity<UserInfoDto> responseUserInfo(String accessToken) {
        log.info("AuthService responseUserInfo");

        ValidationStatus userValidation = userValidation(accessToken);

        if (userValidation.equals(ValidationStatus.NOT_VALID)) {
            throw new CustomException(ErrorCode.SIGN_UP_REQUIRED);     // 202
        }

        Claims claims = jwtUtil.validateToken(accessToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);

        UserInfoDto userInfoDto = userService.buildUserInfoDto(user.get());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userInfoDto);
    }

    public ResponseEntity<Map<String, Object>> responseFriendInfo(User user, User friend) {
        log.info("AuthService responseFriendInfo");

        Map<String, Object> body = new HashMap<>();

        // 친구는 userValidation 검사가 필요 없을까? - ㅇㅇ 회원가입 안 했으면 친구도 못됐을테니까

        boolean relationship = friendRepository.isFriend(user, friend, FriendStatus.ACCEPTED);

        if (!relationship) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","not friend with user")); }

        Optional<Star> star = starRepository.findByUserId(friend.getId());
        Optional<StarBackground> starBackground = starBackgroundRepository.findById(star.get().getBackground().getId());
//        Optional<Characters> character = characterRepository.findByUser(friend);

        String profileUrl = friend.getProfileUrl();
        String characterUrl = friend.getCharacterUrl();

        body.put("userId", friend.getId());
        body.put("userName", friend.getUserName());
        body.put("birthDate", friend.getBirthDate());
        body.put("starBackground", starBackground.get().getName());
        body.put("profile", profileUrl);    // todo: characterUrl
        body.put("character", characterUrl);    // todo: profileUrl

        log.info("body: " + body);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    public Cookie setRefreshToken(String refreshToken) {
        log.info("AuthService setRefreshToken");
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);        // 추후 true로 변경
        cookie.setMaxAge(7 * 24 * 60 * 60);

        return cookie;
    }

    public ResponseCookie addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("None")
                .build();
        log.info("cookie: {}", cookie);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return cookie;
    }

    public ResponseEntity<Map<String, String>> responseAccessTokenStatus(String accessToken) {
        TokenValidateResult tokenStatus = TokenValidateResult.of(accessToken);

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

    public String getEmailByAuthorization(String authorization) {
        if (authorization.startsWith("admin")) { return HeaderUtil.authorizationAdmin(authorization); }

        String accessToken = HeaderUtil.authorizationBearer(authorization);
        Claims token = jwtUtil.validateToken(accessToken);

        return token.getSubject();
    }

    public void userInStation(String stationId, String email) {
        if (stationId == null || stationId.isEmpty()) { throw new CustomException(ErrorCode.INVALID_STATION_ID); }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_FOUND); }

        Optional<Station> station = stationRepository.findById(stationId);
        if (station.isEmpty()) { throw new CustomException(ErrorCode.STATION_NOT_FOUND); }

        UserStationId userStationId = new UserStationId(user.get().getId(), stationId);
        Optional<UserStation> userStation = userStationRepository.findById(userStationId);

        if (userStation.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_IN_STATION); }
    }

    @Transactional
    public void logout(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_FOUND); }
        user.get().setRefreshToken(null);
        userRepository.save(user.get());
    }
}
