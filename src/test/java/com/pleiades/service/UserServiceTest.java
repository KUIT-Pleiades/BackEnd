package com.pleiades.service;

import com.pleiades.dto.*;
import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.entity.*;
import com.pleiades.entity.character.CharacterItem;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.TheItem;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.CharacterItemRepository;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.repository.StarRepository;
import com.pleiades.repository.UserHistoryRepository;
import com.pleiades.strings.FriendStatus;
import com.pleiades.strings.ItemType;
import com.pleiades.strings.ValidationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHistoryRepository userHistoryRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private StarRepository starRepository;

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private TheItemRepository theItemRepository;

    @Mock
    private CharacterItemRepository characterItemRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("getUserByEmail - 사용자가 존재하지 않을 때 예외 발생")
    void getUserByEmail_userNotFound_throwsException() {
        // given
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("getUserByEmail - 정상 케이스")
    void getUserByEmail_validEmail_returnsUser() {
        // given
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserByEmail(email);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("setProfile - userName과 birthDate가 null일 때 예외 발생")
    void setProfile_nullFields_throwsException() {
        // given
        String email = "test@example.com";
        ProfileSettingDto dto = new ProfileSettingDto();
        dto.setUserName(null);
        dto.setBirthDate(null);

        User user = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.setProfile(email, dto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INFORMATION_NOT_VALID);
    }

    @Test
    @DisplayName("setProfile - 정상 케이스")
    void setProfile_validData_returnsSuccess() {
        // given
        String email = "test@example.com";
        ProfileSettingDto dto = new ProfileSettingDto();
        dto.setUserName("새이름");
        dto.setBirthDate(LocalDate.now());

        User user = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        Map<String, String> result = userService.setProfile(email, dto);

        // then
        assertThat(result).containsEntry("message", "profile setting success");
        assertThat(user.getUserName()).isEqualTo("새이름");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("setCharacter - 사용자가 존재하지 않을 때 예외 발생")
    void setCharacter_userNotFound_throwsException() {
        // given
        String email = "test@example.com";
        CharacterDto dto = createCharacterDto();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.setCharacter(email, dto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("setCharacter - Character가 존재하지 않을 때 예외 발생")
    void setCharacter_characterNotFound_throwsException() {
        // given
        String email = "test@example.com";
        CharacterDto dto = createCharacterDto();
        User user = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(characterRepository.findByUser(user)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.setCharacter(email, dto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CHARACTER_NOT_FOUND);
    }

    @Test
    @DisplayName("setCharacter - 정상 케이스")
    void setCharacter_validData_returnsValid() {
        // given
        String email = "test@example.com";
        CharacterDto dto = createCharacterDto();
        User user = User.builder().id("user1").email(email).build();
        Characters character = new Characters();
        character.setId(1L);
        character.setUser(user);

        TheItem skinItem = TheItem.builder().id(1L).name("skin1").type(ItemType.SKIN_COLOR).build();
        TheItem hairItem = TheItem.builder().id(2L).name("hair1").type(ItemType.HAIR).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(characterRepository.findByUser(user)).thenReturn(Optional.of(character));
        when(theItemRepository.findByNameAndType("skin1", ItemType.SKIN_COLOR)).thenReturn(Optional.of(skinItem));
        when(theItemRepository.findByNameAndType("hair1", ItemType.HAIR)).thenReturn(Optional.of(hairItem));
        when(theItemRepository.findByNameAndType(anyString(), any(ItemType.class))).thenReturn(Optional.empty());
        doNothing().when(characterItemRepository).deleteAllByCharacter(character);
        when(characterRepository.save(any(Characters.class))).thenReturn(character);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        ValidationStatus result = userService.setCharacter(email, dto);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
        verify(characterItemRepository).deleteAllByCharacter(character);
        verify(characterRepository).save(character);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("setBackground - 사용자가 존재하지 않을 때 NONE 반환")
    void setBackground_userNotFound_returnsNone() {
        // given
        String email = "test@example.com";
        String backgroundName = "background1";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        ValidationStatus result = userService.setBackground(email, backgroundName);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NONE);
    }

    @Test
    @DisplayName("setBackground - Star가 없을 때 NOT_VALID 반환")
    void setBackground_starNotFound_returnsNotValid() {
        // given
        String email = "test@example.com";
        String backgroundName = "background1";
        User user = User.builder().id("user1").email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(starRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        // when
        ValidationStatus result = userService.setBackground(email, backgroundName);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
    }

    @Test
    @DisplayName("setBackground - Background가 없을 때 NOT_VALID 반환")
    void setBackground_backgroundNotFound_returnsNotValid() {
        // given
        String email = "test@example.com";
        String backgroundName = "background1";
        User user = User.builder().id("user1").email(email).build();
        Star star = new Star();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(starRepository.findByUserId(user.getId())).thenReturn(Optional.of(star));
        when(theItemRepository.findByTypeAndName(ItemType.STAR_BG, backgroundName)).thenReturn(Optional.empty());

        // when
        ValidationStatus result = userService.setBackground(email, backgroundName);

        // then
        assertThat(result).isEqualTo(ValidationStatus.NOT_VALID);
    }

    @Test
    @DisplayName("setBackground - 정상 케이스")
    void setBackground_validData_returnsValid() {
        // given
        String email = "test@example.com";
        String backgroundName = "background1";
        User user = User.builder().id("user1").email(email).build();
        Star star = new Star();
        TheItem background = TheItem.builder().id(1L).name(backgroundName).type(ItemType.STAR_BG).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(starRepository.findByUserId(user.getId())).thenReturn(Optional.of(star));
        when(theItemRepository.findByTypeAndName(ItemType.STAR_BG, backgroundName)).thenReturn(Optional.of(background));
        when(starRepository.save(any(Star.class))).thenReturn(star);

        // when
        ValidationStatus result = userService.setBackground(email, backgroundName);

        // then
        assertThat(result).isEqualTo(ValidationStatus.VALID);
        assertThat(star.getBackground()).isEqualTo(background);
        verify(starRepository).save(star);
    }

    @Test
    @DisplayName("searchUser - 정상 케이스")
    void searchUser_validRequest_returnsUserList() {
        // given
        String userId = "user";
        String email = "test@example.com";
        User currentUser = User.builder().id("user1").email(email).build();
        User searchedUser = User.builder()
                .id("user2")
                .userName("검색유저")
                .profileUrl("profile_url")
                .build();

        when(userRepository.findByIdContainingIgnoreCase(userId)).thenReturn(List.of(searchedUser));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(currentUser));
        when(friendRepository.findAllByUsersIn(currentUser, List.of(searchedUser))).thenReturn(new ArrayList<>());

        // when
        List<SearchUserDto> result = userService.searchUser(userId, email);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user2");
        assertThat(result.get(0).getStatus()).isEqualTo("JUSTHUMAN");
    }

    @Test
    @DisplayName("getStone - 정상 케이스")
    void getStone_validUser_returnsStone() {
        // given
        String email = "test@example.com";
        User user = User.builder().id("user1").email(email).stone(100L).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        Long result = userService.getStone(email);

        // then
        assertThat(result).isEqualTo(100L);
    }

    @Test
    @DisplayName("addStone - 정상 케이스")
    void addStone_validUser_addsStone() {
        // given
        String email = "test@example.com";
        Long stoneToAdd = 50L;
        User user = User.builder().id("user1").email(email).stone(100L).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        userService.addStone(email, stoneToAdd);

        // then
        assertThat(user.getStone()).isEqualTo(150L);
        verify(userRepository).save(user);
    }

    private CharacterDto createCharacterDto() {
        CharacterDto dto = new CharacterDto();
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
        dto.setOutfit(outfit);

        CharacterItemDto item = new CharacterItemDto();
        dto.setItem(item);

        return dto;
    }
}

