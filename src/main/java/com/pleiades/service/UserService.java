package com.pleiades.service;

import com.pleiades.dto.*;
import com.pleiades.dto.character.CharacterFaceDto;
import com.pleiades.dto.character.CharacterItemDto;
import com.pleiades.dto.character.CharacterOutfitDto;
import com.pleiades.entity.*;
import com.pleiades.entity.character.CharacterItem;
import com.pleiades.entity.character.TheItem;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.CharacterItemRepository;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.strings.FriendStatus;
import com.pleiades.strings.ItemType;
import com.pleiades.util.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.pleiades.entity.User;
import com.pleiades.entity.character.Characters;

import com.pleiades.repository.*;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.strings.ValidationStatus;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final FriendRepository friendRepository;
    private final StarBackgroundRepository starBackgroundRepository; private final StarRepository starRepository;

    private final CharacterRepository characterRepository;
    private final TheItemRepository theItemRepository;
    private final CharacterItemRepository characterItemRepository;

    private final ModelMapper modelMapper;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
    }

    @Transactional
    public Map<String, String> setProfile(String email, ProfileSettingDto profileSettingDto){
        User user = getUserByEmail(email);
        if (profileSettingDto.getUserName() != null
        && profileSettingDto.getBirthDate() != null) {
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

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Characters character = characterRepository.findByUser(user).orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));

        List<TheItem> selectedItems = getSelectedItemsFromDto(characterDto);

        // 기존 아이템 제거 -> 기존 관계 테이블 삭제
        characterItemRepository.deleteAllByCharacter(character);

        List<CharacterItem> characterItems = selectedItems.stream()
                .map(item -> new CharacterItem(character, item))
                .collect(Collectors.toCollection(ArrayList::new));

        character.changeCharacterItems(characterItems);
        characterRepository.save(character);

        user.setProfileUrl(characterDto.getProfile());
        user.setCharacterUrl(characterDto.getCharacter());
        userRepository.save(user);

        return ValidationStatus.VALID;
    }

    private List<TheItem> getSelectedItemsFromDto(CharacterDto dto) {
        List<TheItem> items = new ArrayList<>();
        addIfPresent(items, dto.getFace().getSkinColor(), ItemType.SKIN_COLOR);
        addIfPresent(items, dto.getFace().getHair(), ItemType.HAIR);
        addIfPresent(items, dto.getFace().getEyes(), ItemType.EYES);
        addIfPresent(items, dto.getFace().getNose(), ItemType.NOSE);
        addIfPresent(items, dto.getFace().getMouth(), ItemType.MOUTH);
        addIfPresent(items, dto.getFace().getMole(), ItemType.MOLE);

        addIfPresent(items, dto.getOutfit().getTop(), ItemType.TOP);
        addIfPresent(items, dto.getOutfit().getBottom(), ItemType.BOTTOM);
        addIfPresent(items, dto.getOutfit().getSet(), ItemType.SET);
        addIfPresent(items, dto.getOutfit().getShoes(), ItemType.SHOES);

        addIfPresent(items, dto.getItem().getHead(), ItemType.HEAD);
        addIfPresent(items, dto.getItem().getEyesItem(), ItemType.EYES_ITEM);
        addIfPresent(items, dto.getItem().getNeck(), ItemType.NECK);
        addIfPresent(items, dto.getItem().getEars(), ItemType.EARS);
        addIfPresent(items, dto.getItem().getLeftHand(), ItemType.LEFT_HAND);
        addIfPresent(items, dto.getItem().getRightHand(), ItemType.RIGHT_HAND);
        addIfPresent(items, dto.getItem().getLeftWrist(), ItemType.LEFT_WRIST);
        addIfPresent(items, dto.getItem().getRightWrist(), ItemType.RIGHT_WRIST);

        return items;
    }

    private void addIfPresent(List<TheItem> list, String name, ItemType type) {
        if (name == null || name.isBlank()) return;
        theItemRepository.findByNameAndType(name, type).ifPresent(list::add);
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
                .filter(user -> !user.equals(currentUser))
                .map(user -> new SearchUserDto(
                        user.getId(),
                        user.getUserName(),
                        user.getProfileUrl(),
                        includeFriendStatus ? friendStatusMap.getOrDefault(user, "JUSTHUMAN") : null
                ))
                .toList();
    }

    public UserInfoDto buildUserInfoDto(User user) {
        Optional<Star> star = starRepository.findByUserId(user.getId());
        Optional<StarBackground> starBackground = starBackgroundRepository.findById(star.get().getBackground().getId());
        Characters character = characterRepository.findByUser(user).orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));

        UserInfoDto userInfoDto = modelMapper.map(user, UserInfoDto.class);
        log.info("buildUserInfoDTO star BG name: " + starBackground.get().getName());
        userInfoDto.setBackgroundName(starBackground.get().getName());

        List<TheItem> items = character.getCharacterItems().stream()
                .map(CharacterItem::getItem)
                .toList();

        userInfoDto.setFace(makeCharacterFaceDto(items));
        userInfoDto.setOutfit(makeCharacterOutfitDto(items));
        userInfoDto.setItem(makeCharacterItemDto(items));

        return userInfoDto;
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAllUserHistory(String email) {
        log.info("UserService - delete old UserHistory");

        User currentUser = getUserByEmail(email);

        List<UserHistory> toDelete = userHistoryRepository.findByCurrent(currentUser);

        if(toDelete == null || toDelete.isEmpty()) {
            log.info("toDelete is null || empty");
            return ResponseEntity.noContent().build();
        }

        for (UserHistory history : toDelete) {
            userHistoryRepository.delete(history);
        }

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> deleteOldUserHistory(String email, String searchedId) {
        log.info("UserService - delete old UserHistory");

        User currentUser = getUserByEmail(email);
        User searchedUser = userRepository.findById(searchedId).orElse(null);

        if(searchedUser == null){
            log.info("searchedUser == null");
            throw new CustomException(ErrorCode.SEARCH_ID_NOT_FOUND);
        }

        UserHistory toDelete = userHistoryRepository.findByCurrentAndSearched(currentUser, searchedUser).orElse(null);

        if(toDelete == null){
            log.info("toDelete == null");
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
            throw new CustomException(ErrorCode.SEARCH_ID_NOT_FOUND);
        }

        // 기존 검색 기록 조회 - 같은 searchedUser 가 있는지 확인
        Optional<UserHistory> existingHistory = userHistoryRepository.findByCurrentAndSearched(currentUser, searchedUser);

        boolean isFriend = friendRepository.isFriend(currentUser, searchedUser, FriendStatus.ACCEPTED);

        if (existingHistory.isPresent()) {
            // 기존 기록 존재 -> searchCount++ & updatedAt 갱신
            UserHistory history = existingHistory.get();
            history.setSearchCount(history.getSearchCount() + 1);
            history.setUpdatedAt(LocalDateTimeUtil.now());
            userHistoryRepository.save(history);
        } else {
            // 새로운 검색 기록 저장
            UserHistory newHistory = UserHistory.builder()
                    .current(currentUser)
                    .searched(searchedUser)
                    .isFriend(isFriend)
                    .searchCount(1)
                    .updatedAt(LocalDateTimeUtil.now())
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

    private CharacterFaceDto makeCharacterFaceDto(List<TheItem> items) {
        CharacterFaceDto dto = new CharacterFaceDto();
        items.forEach(item -> {
            switch (item.getType()) {
                case SKIN_COLOR -> dto.setSkinColor(item.getName());
                case HAIR -> dto.setHair(item.getName());
                case EYES -> dto.setEyes(item.getName());
                case NOSE -> dto.setNose(item.getName());
                case MOUTH -> dto.setMouth(item.getName());
                case MOLE -> dto.setMole(item.getName());
            }
        });

        return dto;
    }

    private CharacterOutfitDto makeCharacterOutfitDto(List<TheItem> items) {
        CharacterOutfitDto dto = new CharacterOutfitDto();

        items.forEach(item -> {
            switch (item.getType()) {
                case TOP -> dto.setTop(item.getName());
                case BOTTOM -> dto.setBottom(item.getName());
                case SET -> dto.setSet(item.getName());
                case SHOES -> dto.setShoes(item.getName());
            }
        });

        return dto;
    }

    private CharacterItemDto makeCharacterItemDto(List<TheItem> items) {
        CharacterItemDto dto = new CharacterItemDto();

        items.forEach(item -> {
            switch (item.getType()) {
                case HEAD -> dto.setHead(item.getName());
                case EYES_ITEM -> dto.setEyesItem(item.getName());
                case EARS -> dto.setEars(item.getName());
                case NECK -> dto.setNeck(item.getName());
                case LEFT_HAND -> dto.setLeftHand(item.getName());
                case LEFT_WRIST -> dto.setLeftWrist(item.getName());
                case RIGHT_HAND -> dto.setRightHand(item.getName());
                case RIGHT_WRIST -> dto.setRightWrist(item.getName());
            }
        });

        return dto;
    }

    // 캐릭터 생성 예전 버전
//    private Face makeFace() {
//        Face face = new Face();
//        Optional<Skin> skin = skinRepository.findByName(characterDto.getFace().getSkinImg());
//        Optional<Expression> expression = expressionRepository.findByName(characterDto.getFace().getExpressionImg());
//        Optional<Hair> hair = hairRepository.findByName(characterDto.getFace().getHairImg());
//
//        skin.ifPresent(face::setSkin);
//        expression.ifPresent(face::setExpression);
//        hair.ifPresent(face::setHair);
//
//        return face;
//    } // makeOutfit, makeItem

}
