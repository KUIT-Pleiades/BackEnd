package com.pleiades.service.auth;

import com.pleiades.entity.Star;
import com.pleiades.entity.User;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.TheItem;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.*;
import com.pleiades.repository.character.CharacterItemRepository;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.service.UserService;
import com.pleiades.strings.FriendStatus;
import com.pleiades.strings.JwtRole;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private UserStationRepository userStationRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StarRepository starRepository;

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private CharacterItemRepository characterItemRepository;

    @Mock
    private UserHistoryRepository userHistoryRepository;

    @Mock
    private SignalRepository signalRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportHistoryRepository reportHistoryRepository;

    @Mock
    private NaverTokenRepository naverTokenRepository;

    @Mock
    private KakaoTokenRepository kakaoTokenRepository;

    @Mock
    private TheItemRepository theItemRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("refreshToken이 null일 때 PRECONDITION_REQUIRED 반환")
    void responseRefreshTokenStatus_nullToken_returnsPreconditionRequired() {
        // given
        String refreshToken = null;

        // when
        ResponseEntity<Map<String, String>> response = authService.responseRefreshTokenStatus(refreshToken);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED);
        assertThat(response.getBody()).containsEntry("message", "Refresh Token is required");
    }

    @Test
    @DisplayName("refreshToken이 유효하지 않을 때 FORBIDDEN 반환")
    void responseRefreshTokenStatus_invalidToken_returnsForbidden() {
        // given
        String refreshToken = "invalid_token";

        // when
        ResponseEntity<Map<String, String>> response = authService.responseRefreshTokenStatus(refreshToken);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("message", "Refresh token is not valid. Social login is required");
    }

    @Test
    @DisplayName("refreshToken이 유효하고 사용자가 존재할 때 새 토큰 발급")
    void responseRefreshTokenStatus_validTokenAndUserExists_returnsNewTokens() {
        // given
        // TokenValidateResult.of()는 실제 JwtUtil을 사용하여 토큰을 검증하므로
        // 실제 유효한 JWT 토큰을 생성해야 함
        // 주의: 이 테스트는 JWT_SECRET_KEY 환경 변수가 설정되어 있어야 정상 동작함
        String email = "test@example.com";
        
        // 환경 변수 확인
        String secretKey = System.getenv("JWT_SECRET_KEY");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                secretKey != null && !secretKey.isEmpty(),
                "JWT_SECRET_KEY 환경 변수가 설정되지 않아 테스트를 스킵합니다."
        );
        
        // 실제 유효한 refresh token 생성
        JwtUtil testJwtUtil = new JwtUtil();
        String refreshToken = testJwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());
        
        User user = User.builder()
                .id("user1")
                .email(email)
                .refreshToken(refreshToken)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole())).thenReturn("new_access_token");
        when(jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole())).thenReturn("new_refresh_token");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        ResponseEntity<Map<String, String>> response = authService.responseRefreshTokenStatus(refreshToken);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsKey("accessToken");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("refreshToken이 유효하지만 사용자가 존재하지 않을 때 예외 발생")
    void responseRefreshTokenStatus_validTokenButUserNotFound_throwsException() {
        // given
        // TokenValidateResult.of()는 실제 JWT 검증을 수행하므로 실제 유효한 토큰 필요
        String email = "test@example.com";
        
        // 환경 변수 확인
        String secretKey = System.getenv("JWT_SECRET_KEY");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                secretKey != null && !secretKey.isEmpty(),
                "JWT_SECRET_KEY 환경 변수가 설정되지 않아 테스트를 스킵합니다."
        );
        
        // 실제 유효한 refresh token 생성
        JwtUtil testJwtUtil = new JwtUtil();
        String refreshToken = testJwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.responseRefreshTokenStatus(refreshToken))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("refreshToken이 저장된 토큰과 일치하지 않을 때 FORBIDDEN 반환")
    void responseRefreshTokenStatus_tokenMismatch_returnsForbidden() {
        // given
        // TokenValidateResult.of()는 실제 JWT 검증을 수행하므로 실제 유효한 토큰 필요
        String email = "test@example.com";
        
        // 환경 변수 확인
        String secretKey = System.getenv("JWT_SECRET_KEY");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                secretKey != null && !secretKey.isEmpty(),
                "JWT_SECRET_KEY 환경 변수가 설정되지 않아 테스트를 스킵합니다."
        );
        
        // 실제 유효한 refresh token 생성
        JwtUtil testJwtUtil = new JwtUtil();
        String refreshToken = testJwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());
        
        // 시간차를 두어 다른 refresh token 생성 (저장된 토큰과 다름)
        // JWT의 issuedAt이 다르면 다른 토큰이 생성됨
        try {
            Thread.sleep(100); // 100ms 대기하여 issuedAt이 다르게 생성되도록 함
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String differentToken = testJwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole());
        
        // 토큰이 실제로 다른지 확인
        org.junit.jupiter.api.Assumptions.assumeTrue(
                !refreshToken.equals(differentToken),
                "생성된 토큰이 동일하여 테스트를 스킵합니다."
        );
        
        User user = User.builder()
                .id("user1")
                .email(email)
                .refreshToken(refreshToken)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        ResponseEntity<Map<String, String>> response = authService.responseRefreshTokenStatus(differentToken);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("message", "Refresh token is not valid. Social login is required");
    }

    @Test
    @DisplayName("친구 정보 조회 - 친구 관계가 아닐 때 FORBIDDEN 반환")
    void responseFriendInfo_notFriend_returnsForbidden() {
        // given
        User user = User.builder().id("user1").build();
        User friend = User.builder().id("user2").build();

        when(friendRepository.isFriend(user, friend, FriendStatus.ACCEPTED)).thenReturn(false);

        // when
        ResponseEntity<Map<String, Object>> response = authService.responseFriendInfo(user, friend);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("message", "not friend with user");
    }

    @Test
    @DisplayName("친구 정보 조회 - Star가 없을 때 예외 발생")
    void responseFriendInfo_starNotFound_throwsException() {
        // given
        User user = User.builder().id("user1").build();
        User friend = User.builder().id("user2").build();

        when(friendRepository.isFriend(user, friend, FriendStatus.ACCEPTED)).thenReturn(true);
        when(starRepository.findByUserId(friend.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.responseFriendInfo(user, friend))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STAR_NOT_FOUND);
    }

    @Test
    @DisplayName("친구 정보 조회 - 정상 케이스")
    void responseFriendInfo_validRequest_returnsFriendInfo() {
        // given
        User user = User.builder().id("user1").build();
        User friend = User.builder()
                .id("user2")
                .userName("친구")
                .birthDate(java.time.LocalDate.now())
                .profileUrl("profile_url")
                .characterUrl("character_url")
                .build();

        Star star = new Star();
        TheItem background = TheItem.builder().id(1L).name("background1").build();
        star.setBackground(background);

        when(friendRepository.isFriend(user, friend, FriendStatus.ACCEPTED)).thenReturn(true);
        when(starRepository.findByUserId(friend.getId())).thenReturn(Optional.of(star));
        when(theItemRepository.findById(1L)).thenReturn(Optional.of(background));

        // when
        ResponseEntity<Map<String, Object>> response = authService.responseFriendInfo(user, friend);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKeys("userId", "userName", "birthDate", "starBackground", "profile", "character");
    }

    @Test
    @DisplayName("setRefreshToken - 쿠키 생성")
    void setRefreshToken_validToken_returnsCookie() {
        // given
        String refreshToken = "refresh_token";

        // when
        Cookie cookie = authService.setRefreshToken(refreshToken);

        // then
        assertThat(cookie.getName()).isEqualTo("refreshToken");
        assertThat(cookie.getValue()).isEqualTo(refreshToken);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(7 * 24 * 60 * 60);
    }

    @Test
    @DisplayName("addRefreshTokenCookie - ResponseCookie 추가")
    void addRefreshTokenCookie_validToken_addsCookie() {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        String refreshToken = "refresh_token";

        // when
        org.springframework.http.ResponseCookie cookie = authService.addRefreshTokenCookie(response, refreshToken);

        // then
        assertThat(cookie.getName()).isEqualTo("refreshToken");
        assertThat(cookie.getValue()).isEqualTo(refreshToken);
        verify(response).addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Test
    @DisplayName("responseAccessTokenStatus - 토큰이 null일 때 PRECONDITION_REQUIRED 반환")
    void responseAccessTokenStatus_nullToken_returnsPreconditionRequired() {
        // given
        String accessToken = null;

        // when
        ResponseEntity<Map<String, String>> response = authService.responseAccessTokenStatus(accessToken);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED);
    }

    @Test
    @DisplayName("responseAccessTokenStatus - 토큰이 유효하지 않을 때 UNAUTHORIZED 반환")
    void responseAccessTokenStatus_invalidToken_returnsUnauthorized() {
        // given
        String accessToken = "invalid_token";

        // when
        ResponseEntity<Map<String, String>> response = authService.responseAccessTokenStatus(accessToken);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("responseAccessTokenStatus - 사용자가 존재하지 않을 때 ACCEPTED 반환")
    void responseAccessTokenStatus_userNotFound_returnsAccepted() {
        // given
        String accessToken = "valid_token";
        String email = "test@example.com";
        Claims claims = mock(Claims.class);

        when(jwtUtil.validateToken(accessToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        ResponseEntity<Map<String, String>> response = authService.responseAccessTokenStatus(accessToken);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    @DisplayName("responseAccessTokenStatus - 정상 케이스")
    void responseAccessTokenStatus_validTokenAndUserExists_returnsOk() {
        // given
        String accessToken = "valid_token";
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).build();
        Claims claims = mock(Claims.class);

        when(jwtUtil.validateToken(accessToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        ResponseEntity<Map<String, String>> response = authService.responseAccessTokenStatus(accessToken);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("logout - 사용자가 존재하지 않을 때 예외 발생")
    void logout_userNotFound_throwsException() {
        // given
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.logout(email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("logout - 정상 케이스")
    void logout_validUser_clearsRefreshToken() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .id("user1")
                .email(email)
                .refreshToken("refresh_token")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        authService.logout(email);

        // then
        verify(userRepository).save(any(User.class));
        assertThat(user.getRefreshToken()).isNull();
    }

    @Test
    @DisplayName("withdraw - 사용자가 존재하지 않을 때 예외 발생")
    void withdraw_userNotFound_throwsException() {
        // given
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.withdraw(email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("withdraw - 정상 케이스")
    void withdraw_validUser_deletesAllRelatedData() {
        // given
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(characterRepository.findByUser(user)).thenReturn(Optional.empty());
        when(userStationRepository.findStationsWhereUserIsAdmin(user)).thenReturn(java.util.Collections.emptyList());

        // when
        authService.withdraw(email);

        // then
        verify(friendRepository).deleteAllBySenderOrReceiver(user, user);
        verify(signalRepository).deleteAllByReceiver(user);
        verify(signalRepository).deleteAllBySender(user);
        verify(userStationRepository).deleteAllByUser(user);
        verify(userHistoryRepository).deleteAllByCurrent(user);
        verify(reportHistoryRepository).deleteAllByUser(user);
        verify(reportRepository).anonymizeUserFromReports(user);
        verify(starRepository).deleteByUser(user);
        verify(naverTokenRepository).deleteByUser(user);
        verify(kakaoTokenRepository).deleteByUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("userValidation - 사용자가 존재하지 않을 때 NOT_VALID 반환")
    void userValidation_userNotFound_returnsNotValid() {
        // given
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        ValidationStatus result = authService.userValidation(email);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
    }

    @Test
    @DisplayName("userValidation - Star가 없을 때 NOT_VALID 반환")
    void userValidation_starNotFound_returnsNotValid() {
        // given
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(starRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        // when
        ValidationStatus result = authService.userValidation(email);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
    }

    @Test
    @DisplayName("userValidation - Character가 없을 때 NOT_VALID 반환")
    void userValidation_characterNotFound_returnsNotValid() {
        // given
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).build();
        Star star = new Star();
        TheItem background = TheItem.builder().id(1L).build();
        star.setBackground(background);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(starRepository.findByUserId(user.getId())).thenReturn(Optional.of(star));
        when(theItemRepository.findById(1L)).thenReturn(Optional.of(background));
        when(characterRepository.findByUser(user)).thenReturn(Optional.empty());

        // when
        ValidationStatus result = authService.userValidation(email);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
    }

    @Test
    @DisplayName("userValidation - Background가 null일 때 예외 발생")
    void userValidation_backgroundIsNull_throwsException() {
        // given
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).build();
        Star star = new Star();
        star.setBackground(null);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(starRepository.findByUserId(user.getId())).thenReturn(Optional.of(star));

        // when & then
        assertThatThrownBy(() -> authService.userValidation(email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ITEM_NOT_FOUND);
    }

    @Test
    @DisplayName("userValidation - 정상 케이스")
    void userValidation_validUser_returnsValid() {
        // given
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).build();
        Star star = new Star();
        TheItem background = TheItem.builder().id(1L).build();
        star.setBackground(background);
        Characters character = new Characters();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(starRepository.findByUserId(user.getId())).thenReturn(Optional.of(star));
        when(theItemRepository.findById(1L)).thenReturn(Optional.of(background));
        when(characterRepository.findByUser(user)).thenReturn(Optional.of(character));

        // when
        ValidationStatus result = authService.userValidation(email);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
    }
}

