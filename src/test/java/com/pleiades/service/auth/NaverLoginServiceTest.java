package com.pleiades.service.auth;

import com.pleiades.dto.LoginResponseDto;
import com.pleiades.dto.naver.NaverLoginResponseDto;
import com.pleiades.entity.NaverToken;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.exception.NaverRefreshTokenExpiredException;
import com.pleiades.repository.NaverTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.JwtRole;
import com.pleiades.util.JwtUtil;
import com.pleiades.util.NaverApiUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NaverLoginServiceTest {

    @Mock
    private NaverApiUtil naverApiUtil;

    @Mock
    private NaverTokenRepository naverTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private NaverLoginService naverLoginService;

    @Test
    @DisplayName("handleNaverLoginCallback - 토큰을 받아오지 못했을 때 예외 발생")
    void handleNaverLoginCallback_failedToGetTokens_throwsException() {
        // given
        String code = "auth_code";

        when(naverApiUtil.getTokens(code)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> naverLoginService.handleNaverLoginCallback(code))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("handleNaverLoginCallback - accessToken이 null일 때 예외 발생")
    void handleNaverLoginCallback_accessTokenIsNull_throwsException() {
        // given
        String code = "auth_code";
        Map<String, String> tokens = Map.of("refresh_token", "refresh_token");

        when(naverApiUtil.getTokens(code)).thenReturn(tokens);

        // when & then
        assertThatThrownBy(() -> naverLoginService.handleNaverLoginCallback(code))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("handleNaverLoginCallback - 기존 유저 로그인")
    void handleNaverLoginCallback_existingUser_returnsLoginResponse() {
        // given
        String code = "auth_code";
        String email = "test@example.com";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        Map<String, String> tokens = Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        );

        NaverLoginResponseDto userInfo = new NaverLoginResponseDto();
        userInfo.setEmail(email);

        User user = User.builder().id("user1").email(email).build();
        NaverToken naverToken = NaverToken.builder()
                .id(1L)
                .email(email)
                .refreshToken(refreshToken)
                .user(user)
                .build();

        when(naverApiUtil.getTokens(code)).thenReturn(tokens);
        when(naverApiUtil.getUserInfo(accessToken)).thenReturn(userInfo);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(naverTokenRepository.findByEmail(email)).thenReturn(Optional.of(naverToken));
        when(naverTokenRepository.save(any(NaverToken.class))).thenReturn(naverToken);
        when(jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole())).thenReturn("jwt_access_token");
        when(jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole())).thenReturn("jwt_refresh_token");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        LoginResponseDto result = naverLoginService.handleNaverLoginCallback(code);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotNull();
        verify(naverTokenRepository).save(any(NaverToken.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("handleNaverLoginCallback - 기존 유저인데 NaverToken이 없을 때 예외 발생")
    void handleNaverLoginCallback_existingUserWithoutNaverToken_throwsException() {
        // given
        String code = "auth_code";
        String email = "test@example.com";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        Map<String, String> tokens = Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        );

        NaverLoginResponseDto userInfo = new NaverLoginResponseDto();
        userInfo.setEmail(email);

        User user = User.builder().id("user1").email(email).build();

        when(naverApiUtil.getTokens(code)).thenReturn(tokens);
        when(naverApiUtil.getUserInfo(accessToken)).thenReturn(userInfo);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(naverTokenRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> naverLoginService.handleNaverLoginCallback(code))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("handleNaverLoginCallback - 회원가입하지 않은 유저 (NaverToken만 존재)")
    void handleNaverLoginCallback_userWithoutSignup_returnsLoginResponse() {
        // given
        String code = "auth_code";
        String email = "test@example.com";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        Map<String, String> tokens = Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        );

        NaverLoginResponseDto userInfo = new NaverLoginResponseDto();
        userInfo.setEmail(email);

        NaverToken naverToken = NaverToken.builder()
                .id(1L)
                .email(email)
                .refreshToken("old_refresh_token")
                .build();

        when(naverApiUtil.getTokens(code)).thenReturn(tokens);
        when(naverApiUtil.getUserInfo(accessToken)).thenReturn(userInfo);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(naverTokenRepository.findByEmail(email)).thenReturn(Optional.of(naverToken));
        when(naverTokenRepository.save(any(NaverToken.class))).thenReturn(naverToken);
        when(jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole())).thenReturn("jwt_access_token");
        when(jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole())).thenReturn("jwt_refresh_token");

        // when
        LoginResponseDto result = naverLoginService.handleNaverLoginCallback(code);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotNull();
        verify(naverTokenRepository).save(any(NaverToken.class));
    }

    @Test
    @DisplayName("handleNaverLoginCallback - 신규 유저")
    void handleNaverLoginCallback_newUser_returnsLoginResponse() {
        // given
        String code = "auth_code";
        String email = "test@example.com";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        Map<String, String> tokens = Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        );

        NaverLoginResponseDto userInfo = new NaverLoginResponseDto();
        userInfo.setEmail(email);

        NaverToken naverToken = NaverToken.builder()
                .id(1L)
                .email(email)
                .refreshToken(refreshToken)
                .build();

        when(naverApiUtil.getTokens(code)).thenReturn(tokens);
        when(naverApiUtil.getUserInfo(accessToken)).thenReturn(userInfo);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(naverTokenRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(naverTokenRepository.save(any(NaverToken.class))).thenReturn(naverToken);
        when(jwtUtil.generateAccessToken(email, JwtRole.ROLE_USER.getRole())).thenReturn("jwt_access_token");
        when(jwtUtil.generateRefreshToken(email, JwtRole.ROLE_USER.getRole())).thenReturn("jwt_refresh_token");

        // when
        LoginResponseDto result = naverLoginService.handleNaverLoginCallback(code);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotNull();
        verify(naverTokenRepository).save(any(NaverToken.class));
    }

    @Test
    @DisplayName("handleNaverRefreshTokenLogin - refreshToken이 존재하지 않을 때 예외 발생")
    void handleNaverRefreshTokenLogin_tokenNotFound_throwsException() {
        // given
        String refreshToken = "refresh_token";

        when(naverTokenRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> naverLoginService.handleNaverRefreshTokenLogin(refreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DB에 네이버 Refresh Token 존재 X");
    }

    @Test
    @DisplayName("handleNaverRefreshTokenLogin - accessToken을 받아오지 못했을 때 예외 발생")
    void handleNaverRefreshTokenLogin_failedToGetAccessToken_throwsException() {
        // given
        String refreshToken = "refresh_token";
        NaverToken naverToken = NaverToken.builder()
                .id(1L)
                .email("test@example.com")
                .refreshToken(refreshToken)
                .build();

        when(naverTokenRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(naverToken));
        when(naverApiUtil.getValidAccessToken(naverToken)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> naverLoginService.handleNaverRefreshTokenLogin(refreshToken))
                .isInstanceOf(NaverRefreshTokenExpiredException.class)
                .hasMessageContaining("네이버 access token 존재 X");
    }

    @Test
    @DisplayName("handleNaverRefreshTokenLogin - 정상 케이스")
    void handleNaverRefreshTokenLogin_validToken_returnsUserInfo() {
        // given
        String refreshToken = "refresh_token";
        String accessToken = "new_access_token";
        NaverToken naverToken = NaverToken.builder()
                .id(1L)
                .email("test@example.com")
                .refreshToken(refreshToken)
                .build();

        NaverLoginResponseDto userInfo = new NaverLoginResponseDto();
        userInfo.setEmail("test@example.com");

        when(naverTokenRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(naverToken));
        when(naverApiUtil.getValidAccessToken(naverToken)).thenReturn(accessToken);
        when(naverTokenRepository.save(any(NaverToken.class))).thenReturn(naverToken);
        when(naverApiUtil.getUserInfo(accessToken)).thenReturn(userInfo);

        // when
        NaverLoginResponseDto result = naverLoginService.handleNaverRefreshTokenLogin(refreshToken);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(naverTokenRepository).save(any(NaverToken.class));
    }
}

