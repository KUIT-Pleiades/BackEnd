package com.pleiades.service.auth;

import com.pleiades.dto.UserInfoDto;
import com.pleiades.entity.*;
import com.pleiades.entity.character.CharacterItem;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.TheItem;

import com.pleiades.repository.*;
import com.pleiades.repository.character.CharacterItemRepository;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.repository.character.TheItemRepository;

import com.pleiades.strings.ItemType;
import com.pleiades.strings.ValidationStatus;
import com.pleiades.util.LocalDateTimeUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Service
public class SignupService {

    private final UserRepository userRepository;
    private final StarRepository starRepository;
    private final CharacterRepository characterRepository;
    private final TheItemRepository theItemRepository;

    private final KakaoTokenRepository kakaoTokenRepository;
    private final NaverTokenRepository naverTokenRepository;

    private final EntityManager entityManager;

    @Transactional
    public ValidationStatus signup(String email, UserInfoDto userInfoDto) {
        log.info("signup으로 온 email: " + email);
        log.info("BG name from front: " + userInfoDto.getBackgroundName());

        // user 중복 생성 방지
        if(userRepository.findByEmail(email).isPresent()){ return ValidationStatus.DUPLICATE; }

        // 소셜 토큰 검증
        Optional<NaverToken> naverToken = naverTokenRepository.findByEmail(email);
        Optional<KakaoToken> kakaoToken = kakaoTokenRepository.findByEmail(email);

        // 소셜 토큰 없음
        if (naverToken.isEmpty() && kakaoToken.isEmpty()) { return ValidationStatus.NOT_VALID; }

        log.info("social token 존재함");

        // 소셜 토큰 존재
        User user = createUser(email, userInfoDto);
        entityManager.flush();

        naverToken.ifPresent(token -> setNaverToken(token, user));
        kakaoToken.ifPresent(token -> setKakaoToken(token, user));

        createStar(user, userInfoDto);
        createCharacter(user, userInfoDto);

        return ValidationStatus.VALID;
    }

    private User createUser(String email, UserInfoDto userInfoDto) {
        User user = User.builder()
                .id(userInfoDto.getUserId())
                .email(email)
                .userName(userInfoDto.getUserName())
                .birthDate(userInfoDto.getBirthDate())
                .createdDate(LocalDateTimeUtil.today())
                .profileUrl(userInfoDto.getProfile())
                .characterUrl(userInfoDto.getCharacter())
                .coin(0L)
                .stone(200L)
                .build();
        userRepository.save(user);
        log.info("User 저장 완료 - id: {}", user.getId());
        return user;
    }

    private void createStar(User user, UserInfoDto userInfoDto) {
        Star star = new Star();
        star.setUser(user);

        Optional<TheItem> background = theItemRepository.findByTypeAndName(ItemType.STAR_BG, userInfoDto.getBackgroundName());
        background.ifPresent(star::setBackground);

        starRepository.save(star);
        log.info("Star 세팅 완료 - id: {}", star.getId());
    }

    private void createCharacter(User user, UserInfoDto userInfoDto) {
        Characters character = new Characters();
        character.setUser(user);
        characterRepository.save(character); // 먼저 저장하고 ID 확보

        // DTO로부터 사용자가 고른 item들 추출
        List<TheItem> items = extractSelectedItems(userInfoDto);

        log.info("extractSelectedItems 완료");

        // character랑 item으로 CI에 column 추가
        items.forEach(item -> {
            CharacterItem ci = CharacterItem.builder()
                    .item(item)
                    .build(); 
            character.addCharacterItem(ci); // 양방향 연결 포함
        });

        log.info("addCharacterItem 완료");

        characterRepository.save(character);

        log.info("Character 세팅 완료 - id: {}", character.getId());
    }

    private List<TheItem> extractSelectedItems(UserInfoDto dto) {
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
        addIfPresent(items, dto.getItem().getEars(), ItemType.EARS);
        addIfPresent(items, dto.getItem().getNeck(), ItemType.NECK);
        addIfPresent(items, dto.getItem().getLeftHand(), ItemType.LEFT_HAND);
        addIfPresent(items, dto.getItem().getLeftWrist(), ItemType.LEFT_WRIST);
        addIfPresent(items, dto.getItem().getRightHand(), ItemType.RIGHT_HAND);
        addIfPresent(items, dto.getItem().getRightWrist(), ItemType.RIGHT_WRIST);

        return items;
    }

    private void addIfPresent(List<TheItem> list, String name, ItemType type) {
        if (name == null || name.isBlank()) return;
        theItemRepository.findByNameAndType(name, type).ifPresent(list::add);
    }


    private void setNaverToken(NaverToken naverToken, User user) {
        naverToken.setUser(user);
        log.info("네이버 token saved: " + naverToken.getId());
        naverTokenRepository.save(naverToken);
    }

    private void setKakaoToken(KakaoToken kakaoToken, User user) {
        kakaoToken.setUser(user);
        log.info("카카오 token saved: " + kakaoToken.getId());
        kakaoTokenRepository.save(kakaoToken);
    }
}
