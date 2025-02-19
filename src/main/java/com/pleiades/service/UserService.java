package com.pleiades.service;

import com.pleiades.dto.ProfileSettingDto;
import com.pleiades.dto.SearchUserDto;
import com.pleiades.entity.*;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.FriendStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.pleiades.dto.CharacterDto;
import com.pleiades.entity.User;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.Item.Item;
import com.pleiades.entity.character.face.Expression;
import com.pleiades.entity.character.face.Face;
import com.pleiades.entity.character.face.Hair;
import com.pleiades.entity.character.face.Skin;
import com.pleiades.entity.character.outfit.Bottom;
import com.pleiades.entity.character.outfit.Outfit;
import com.pleiades.entity.character.outfit.Shoes;
import com.pleiades.entity.character.outfit.Top;
import com.pleiades.repository.*;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.repository.character.face.ExpressionRepository;
import com.pleiades.repository.character.face.HairRepository;
import com.pleiades.repository.character.face.SkinRepository;
import com.pleiades.repository.character.item.*;
import com.pleiades.repository.character.outfit.BottomRepository;
import com.pleiades.repository.character.outfit.ShoesRepository;
import com.pleiades.repository.character.outfit.TopRepository;
import com.pleiades.strings.ValidationStatus;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository; private final UserHistoryRepository userHistoryRepository;
    private final FriendRepository friendRepository;
    private final HeadRepository headRepository; private final EyesRepository eyesRepository; private final EarsRepository earsRepository; private final NeckRepository neckRepository;
    private final LeftWristRepository leftWristRepository; private final RightWristRepository rightWristRepository; private final LeftHandRepository leftHandRepository;private final RightHandRepository rightHandRepository;
    private final StarBackgroundRepository starBackgroundRepository; private final StarRepository starRepository;
    private final CharacterRepository characterRepository;
    private final SkinRepository skinRepository; private final ExpressionRepository expressionRepository; private final HairRepository hairRepository;
    private final TopRepository topRepository; private final BottomRepository bottomRepository; private final ShoesRepository shoesRepository;
    private final ItemRepository itemRepository;

    private final KakaoTokenRepository kakaoTokenRepository;
    private final NaverTokenRepository naverTokenRepository;

    private final EntityManager entityManager;

    private CharacterDto characterDto = null;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
    }

    @Transactional
    public Map<String, String> setProfile(String email, ProfileSettingDto profileSettingDto){
        User user = getUserByEmail(email);
        if (profileSettingDto.getUserId() != null
        && profileSettingDto.getUserName() != null
        && profileSettingDto.getBirthDate() != null) {
            user.setId(profileSettingDto.getUserId());
            user.setUserName(profileSettingDto.getUserName());
            user.setBirthDate(profileSettingDto.getBirthDate());
        }
        else{
            throw new CustomException(ErrorCode.INFORMATION_NOT_VALID);
        }
        userRepository.save(user);
        return Map.of("message","profile setting success");
    }

    @Transactional
    public ValidationStatus setCharacter(String email, CharacterDto characterDto) {
        this.characterDto = characterDto;

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { return ValidationStatus.NONE; }

        Optional<Characters> characterOpt = characterRepository.findByUser(user.get());
        if (characterOpt.isEmpty()) { return ValidationStatus.NOT_VALID; }

        Characters character = characterOpt.get();

        Face face = makeFace();
        Outfit outfit = makeOutfit();
        Item item = makeItem();

        character.setFace(face);
        character.setOutfit(outfit);
        character.setItem(item);
        characterRepository.save(character);

        user.get().setProfileUrl(characterDto.getProfile());
        user.get().setCharacterUrl(characterDto.getCharacter());
        userRepository.save(user.get());

        return ValidationStatus.VALID;
    }

    public ValidationStatus setBackground(String email, String backgroundName) {
        log.info("setBackground");
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            log.info("user not found");
            return ValidationStatus.NONE;
        }

        Optional<Star> star = starRepository.findByUserId(user.get().getId());
        if (star.isEmpty()) {
            log.info("star not found");
            return ValidationStatus.NOT_VALID;
        }

        Optional<StarBackground> background = starBackgroundRepository.findByName(backgroundName);
        if (background.isEmpty()) {
            log.info("background not found");
            return ValidationStatus.NOT_VALID;
        }

        background.ifPresent(star.get()::setBackground);

        starRepository.save(star.get());

        return ValidationStatus.VALID;
    }

    // 사용자 ID 검색
    @Transactional
    public List<SearchUserDto> searchUser(String userId, String email) {
        List<User> users = userRepository.findByIdContainingIgnoreCase(userId);
        User currentUser = getUserByEmail(email);
        return buildSearchUserDto(users, currentUser, true); // FriendStatus 포함
    }

    // 최근 검색 기록
    @Transactional
    public List<SearchUserDto> searchUserHistory(String email) {
        User currentUser = getUserByEmail(email);
        List<UserHistory> histories = userHistoryRepository.findByCurrentOrderByUpdatedAtDesc(currentUser);

        List<User> searchedUsers = histories.stream()
                .map(UserHistory::getSearched)
                .toList();

        return buildSearchUserDto(searchedUsers, currentUser, false);
    }

    private List<SearchUserDto> buildSearchUserDto(List<User> users, User currentUser, boolean includeFriendStatus) {
        Map<User, String> friendStatusMap = new HashMap<>();

        if (includeFriendStatus) {
            List<Friend> friends = users.isEmpty() ? new ArrayList<>() : friendRepository.findAllByUsersIn(currentUser, users);
            for (Friend friend : friends) {
                if (friend.getSender().equals(currentUser)) {
                    if (friend.getStatus() == FriendStatus.PENDING)
                        friendStatusMap.put(friend.getReceiver(), "SENT");
                    else if (friend.getStatus() == FriendStatus.ACCEPTED)
                        friendStatusMap.put(friend.getReceiver(), "FRIEND");
                } else if (friend.getReceiver().equals(currentUser)) {
                    if (friend.getStatus() == FriendStatus.PENDING)
                        friendStatusMap.put(friend.getSender(), "RECEIVED");
                    else if (friend.getStatus() == FriendStatus.ACCEPTED)
                        friendStatusMap.put(friend.getSender(), "FRIEND");
                }
            }
        }

        return users.stream()
                .map(user -> new SearchUserDto(
                        user.getId(),
                        user.getUserName(),
                        user.getProfileUrl(),
                        includeFriendStatus ? friendStatusMap.getOrDefault(user, "JUSTHUMAN") : null
                ))
                .toList();
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> deleteOldUserHistory(String email, String searchedId) {
        log.info("UserService - delete old UserHistory");

        User currentUser = getUserByEmail(email);
        User searchedUser = userRepository.findById(searchedId).orElse(null);

        if(searchedUser == null){
            log.info("searchedUser: null");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","searchedId doesn't exist"));
        }

        UserHistory toDelete = userHistoryRepository.findByCurrentAndSearched(currentUser, searchedUser).orElse(null);

        if(toDelete == null){
            log.info("toDelete: null");
            return ResponseEntity.noContent().build();
        }
        userHistoryRepository.delete(toDelete);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> addUserHistory(String email, String searchedId) {
        log.info("UserService - addUserHistory");

        User currentUser = getUserByEmail(email);
        User searchedUser = userRepository.findById(searchedId).orElse(null);

        if(searchedUser == null){
            log.info("history add: searchedUser == null");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","searchedId doesn't exist"));
        }

        // 기존 검색 기록 조회 - 같은 searchedUser 가 있는지 확인
        Optional<UserHistory> existingHistory = userHistoryRepository.findByCurrentAndSearched(currentUser, searchedUser);

        boolean isFriend = friendRepository.isFriend(currentUser, searchedUser, FriendStatus.ACCEPTED);

        if (existingHistory.isPresent()) {
            // 기존 기록 존재 -> searchCount++ & updatedAt 갱신
            UserHistory history = existingHistory.get();
            history.setSearchCount(history.getSearchCount() + 1);
            history.setUpdatedAt(LocalDateTime.now());
            userHistoryRepository.save(history);
        } else {
            // 새로운 검색 기록 저장
            UserHistory newHistory = UserHistory.builder()
                    .current(currentUser)
                    .searched(searchedUser)
                    .isFriend(isFriend)
                    .searchCount(1)
                    .updatedAt(LocalDateTime.now())
                    .build();
            userHistoryRepository.save(newHistory);
        }

        // 최신 10개 유지 (초과 시 삭제)
        deleteOldUserHistoryIfNeeded(currentUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message","added successfully"));
    }

    @Transactional
    public void deleteOldUserHistoryIfNeeded(User currentUser) {
        List<UserHistory> userHistories = userHistoryRepository.findByCurrentOrderByUpdatedAtDesc(currentUser);

        if (userHistories.size() > 10) {
            // 최신 10개를 제외한 나머지 삭제
            List<UserHistory> toDelete = userHistories.subList(10, userHistories.size());
            userHistoryRepository.deleteAll(toDelete);
        }
    }

    private Face makeFace() {
        Face face = new Face();
        Optional<Skin> skin = skinRepository.findByName(characterDto.getFace().getSkinImg());
        Optional<Expression> expression = expressionRepository.findByName(characterDto.getFace().getExpressionImg());
        Optional<Hair> hair = hairRepository.findByName(characterDto.getFace().getHairImg());

        skin.ifPresent(face::setSkin);
        expression.ifPresent(face::setExpression);
        hair.ifPresent(face::setHair);

        return face;
    }

    private Outfit makeOutfit() {
        Outfit outfit = new Outfit();

        Optional<Top> top = topRepository.findByName(characterDto.getOutfit().getTopImg());
        Optional<Bottom> bottom = bottomRepository.findByName(characterDto.getOutfit().getBottomImg());
        Optional<Shoes> shoes = shoesRepository.findByName(characterDto.getOutfit().getShoesImg());

        top.ifPresent(outfit::setTop);
        bottom.ifPresent(outfit::setBottom);
        shoes.ifPresent(outfit::setShoes);

        return outfit;
    }

    private Item makeItem() {
        Item item = new Item();

        item.setHead(headRepository.findByName(characterDto.getItem().getHeadImg()).orElse(null));
        item.setEyes(eyesRepository.findByName(characterDto.getItem().getEyesImg()).orElse(null));
        item.setEars(earsRepository.findByName(characterDto.getItem().getEarsImg()).orElse(null));
        item.setNeck(neckRepository.findByName(characterDto.getItem().getNeckImg()).orElse(null));
        item.setLeftWrist(leftWristRepository.findByName(characterDto.getItem().getLeftWristImg()).orElse(null));
        item.setRightWrist(rightWristRepository.findByName(characterDto.getItem().getRightWristImg()).orElse(null));
        item.setLeftHand(leftHandRepository.findByName(characterDto.getItem().getLeftHandImg()).orElse(null));
        item.setRightHand(rightHandRepository.findByName(characterDto.getItem().getRightHandImg()).orElse(null));

        return item;
    }
}
