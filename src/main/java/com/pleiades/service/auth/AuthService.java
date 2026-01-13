package com.pleiades.service.auth;

import com.pleiades.dto.UserInfoDto;
import com.pleiades.entity.Star;
import com.pleiades.entity.Station;
import com.pleiades.entity.User;
import com.pleiades.entity.User_Station.UserStation;
import com.pleiades.entity.User_Station.UserStationId;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.TheItem;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.model.TokenValidateResult;
import com.pleiades.repository.*;
import com.pleiades.repository.character.CharacterItemRepository;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.service.UserService;
import com.pleiades.service.station.UserStationService;
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
    private final UserRepository userRepository;
    private final StarRepository starRepository;
    private final CharacterRepository characterRepository;
    private final CharacterItemRepository characterItemRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final SignalRepository signalRepository;
    private final ReportRepository reportRepository;
    private final ReportHistoryRepository reportHistoryRepository;
    private final NaverTokenRepository naverTokenRepository;
    private final KakaoTokenRepository kakaoTokenRepository;
    private final TheItemRepository theItemRepository;

    private final UserStationService userStationService;

    private final JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<Map<String, String>> responseRefreshTokenStatus(String refreshToken) {
        log.info("AuthService responseRefreshTokenStatus");

        Map<String, String> body = new HashMap<>();

        TokenValidateResult tokenStatus = TokenValidateResult.of(refreshToken, jwtUtil);

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

    @Transactional
    public ResponseEntity<Map<String, Object>> responseFriendInfo(User user, User friend) {
        log.info("AuthService responseFriendInfo");

        Map<String, Object> body = new HashMap<>();

        // 친구는 userValidation 검사가 필요 없을까? - ㅇㅇ 회원가입 안 했으면 친구도 못됐을테니까

        boolean relationship = friendRepository.isFriend(user, friend, FriendStatus.ACCEPTED);

        if (!relationship) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","not friend with user")); }

        Optional<Star> star = starRepository.findByUserId(friend.getId());
        if (star.isEmpty()) throw new CustomException(ErrorCode.STAR_NOT_FOUND);

        Optional<TheItem> starBackground = theItemRepository.findById(star.get().getBackground().getId());
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

    @Transactional
    public Cookie setRefreshToken(String refreshToken) {
        log.info("AuthService setRefreshToken");
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);        // 추후 true로 변경
        cookie.setMaxAge(7 * 24 * 60 * 60);

        return cookie;
    }

    @Transactional
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

    @Transactional
    public ResponseEntity<Map<String, String>> responseAccessTokenStatus(String accessToken) {
        TokenValidateResult tokenStatus = TokenValidateResult.of(accessToken,  jwtUtil);

        if (tokenStatus.getValidationStatus().equals(ValidationStatus.NONE)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();      // 428
        }

        if (tokenStatus.getValidationStatus().equals(ValidationStatus.NOT_VALID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();      // 401
        }

        Claims claims = jwtUtil.validateToken(accessToken);
        String email = claims.getSubject();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ResponseEntity.status(HttpStatus.ACCEPTED).build(); }   // user 없음: 202

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    public void logout(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { throw new CustomException(ErrorCode.USER_NOT_FOUND); }
        user.get().setRefreshToken(null);
        userRepository.save(user.get());
    }

    @Transactional
    public void withdraw(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->new CustomException(ErrorCode.USER_NOT_FOUND));

        // character, item
        characterRepository.findByUser(user).ifPresent(character -> {
            characterItemRepository.deleteAllByCharacter(character);
            characterRepository.delete(character);
        });

        // friend, signal
        friendRepository.deleteAllBySenderOrReceiver(user, user);
        signalRepository.deleteAllByReceiver(user);
        signalRepository.deleteAllBySender(user);

        // station 탈퇴
        userStationService.leaveAllStations(user);

        // UserStation (isAdmin -> delete station)
        // user_station 먼저 지우기
        userStationRepository.deleteAllByUser(user);

        // 그리고 station 지우기
        List<Station> stationsToDelete = userStationRepository.findStationsWhereUserIsAdmin(user);
        stationRepository.deleteAll(stationsToDelete);

        // UserHistory
        userHistoryRepository.deleteAllByCurrent(user);

        // Report (user 비식별 처리), ReportHistory
        reportHistoryRepository.deleteAllByUser(user);
        reportRepository.anonymizeUserFromReports(user);

        // star, token
        starRepository.deleteByUser(user);
        naverTokenRepository.deleteByUser(user);
        kakaoTokenRepository.deleteByUser(user);

        userRepository.delete(user);
    }



    // 회원가입 여부
    @Transactional
    public ValidationStatus userValidation(String email) {
        log.info("AuthService userValidation");

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

        TheItem bg = star.get().getBackground();

        if (bg == null) {
            log.info("no connected star background");
            throw new CustomException(ErrorCode.ITEM_NOT_FOUND);
        }

        Optional<TheItem> starBackground = theItemRepository.findById(star.get().getBackground().getId());

        if (starBackground.isEmpty()) {
            log.info("star background not existing");
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
        }

        Optional<Characters> character = characterRepository.findByUser(user.get());

        if (character.isEmpty()) {
            log.info("no character");
            return ValidationStatus.NOT_VALID;
        }

        return ValidationStatus.VALID;
    }

}
