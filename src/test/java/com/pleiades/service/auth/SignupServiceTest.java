package com.pleiades.service.auth;

import com.pleiades.dto.UserInfoDto;
import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.entity.KakaoToken;
import com.pleiades.entity.NaverToken;
import com.pleiades.entity.User;
import com.pleiades.entity.character.TheItem;
import com.pleiades.repository.KakaoTokenRepository;
import com.pleiades.repository.NaverTokenRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.repository.StarRepository;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.ValidationStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StarRepository starRepository;

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private TheItemRepository theItemRepository;

    @Mock
    private KakaoTokenRepository kakaoTokenRepository;

    @Mock
    private NaverTokenRepository naverTokenRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private SignupService signupService;

    @Test
    @DisplayName("signup - 이미 존재하는 유저일 때 DUPLICATE 반환")
    void signup_existingUser_returnsDuplicate() {
        // given
        String email = "test@example.com";
        UserInfoDto userInfoDto = createUserInfoDto();
        User existingUser = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // when
        ValidationStatus result = signupService.signup(email, userInfoDto);

        // then
        assertThat(result).isEqualTo(ValidationStatus.DUPLICATE);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("signup - 소셜 토큰이 없을 때 NOT_VALID 반환")
    void signup_noSocialToken_returnsNotValid() {
        // given
        String email = "test@example.com";
        UserInfoDto userInfoDto = createUserInfoDto();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(naverTokenRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(kakaoTokenRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        ValidationStatus result = signupService.signup(email, userInfoDto);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("signup - NaverToken이 있는 경우 정상 처리")
    void signup_withNaverToken_returnsValid() {
        // given
        String email = "test@example.com";
        UserInfoDto userInfoDto = createUserInfoDto();

        NaverToken naverToken = NaverToken.builder()
                .id(1L)
                .email(email)
                .refreshToken("refresh_token")
                .build();

        TheItem background = TheItem.builder()
                .id(1L)
                .name("background1")
                .type(ItemType.STAR_BG)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(naverTokenRepository.findByEmail(email)).thenReturn(Optional.of(naverToken));
        when(kakaoTokenRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(theItemRepository.findByTypeAndName(ItemType.STAR_BG, userInfoDto.getBackgroundName()))
                .thenReturn(Optional.of(background));
        when(starRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(characterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(theItemRepository.findByNameAndType(anyString(), any(ItemType.class)))
                .thenReturn(Optional.empty());

        // when
        ValidationStatus result = signupService.signup(email, userInfoDto);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
        verify(userRepository).save(any(User.class));
        verify(starRepository).save(any());
        verify(characterRepository, atLeastOnce()).save(any());
        verify(naverTokenRepository).save(naverToken);
    }

    @Test
    @DisplayName("signup - KakaoToken이 있는 경우 정상 처리")
    void signup_withKakaoToken_returnsValid() {
        // given
        String email = "test@example.com";
        UserInfoDto userInfoDto = createUserInfoDto();

        KakaoToken kakaoToken = KakaoToken.builder()
                .id(1L)
                .email(email)
                .refreshToken("refresh_token")
                .build();

        TheItem background = TheItem.builder()
                .id(1L)
                .name("background1")
                .type(ItemType.STAR_BG)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(naverTokenRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(kakaoTokenRepository.findByEmail(email)).thenReturn(Optional.of(kakaoToken));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(theItemRepository.findByTypeAndName(ItemType.STAR_BG, userInfoDto.getBackgroundName()))
                .thenReturn(Optional.of(background));
        when(starRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(characterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(theItemRepository.findByNameAndType(anyString(), any(ItemType.class)))
                .thenReturn(Optional.empty());

        // when
        ValidationStatus result = signupService.signup(email, userInfoDto);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
        verify(userRepository).save(any(User.class));
        verify(starRepository).save(any());
        verify(characterRepository, atLeastOnce()).save(any());
        verify(kakaoTokenRepository).save(kakaoToken);
    }

    private UserInfoDto createUserInfoDto() {
        UserInfoDto dto = new UserInfoDto();
        dto.setUserId("user1");
        dto.setUserName("테스트유저");
        dto.setBirthDate(LocalDate.now());
        dto.setBackgroundName("background1");
        dto.setProfile("https://gateway.pinata.cloud/ipfs/profile");
        dto.setCharacter("https://gateway.pinata.cloud/ipfs/character");

        CharacterFaceDto face = new CharacterFaceDto();
        face.setSkinColor("skin1");
        face.setHair("hair1");
        face.setEyes("eyes1");
        face.setNose("nose1");
        face.setMouth("mouth1");
        dto.setFace(face);

        CharacterOutfitDto outfit = new CharacterOutfitDto();
        outfit.setTop("top1");
        outfit.setBottom("bottom1");
        dto.setOutfit(outfit);

        CharacterItemDto item = new CharacterItemDto();
        item.setHead("head1");
        dto.setItem(item);

        return dto;
    }
}

