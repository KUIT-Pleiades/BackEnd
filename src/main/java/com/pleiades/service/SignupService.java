package com.pleiades.service;

import com.pleiades.dto.UserInfoDto;
import com.pleiades.entity.*;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.Item.*;
import com.pleiades.entity.character.face.Face;
import com.pleiades.entity.character.outfit.Outfit;
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
    private final HeadRepository headRepository;
    private final EyesRepository eyesRepository;
    private final EarsRepository earsRepository;
    private final NeckRepository neckRepository;
    private final LeftWristRepository leftWristRepository;
    private final RightWristRepository rightWristRepository;
    private final LeftHandRepository leftHandRepository;
    private final RightHandRepository rightHandRepository;
    private final StarBackgroundRepository starBackgroundRepository;
    private final UserRepository userRepository;
    private final StarRepository starRepository;
    private final CharacterRepository characterRepository;
    private final SkinRepository skinRepository;
    private final ExpressionRepository expressionRepository;
    private final HairRepository hairRepository;
    private final TopRepository topRepository;
    private final BottomRepository bottomRepository;
    private final ShoesRepository shoesRepository;
    private final ItemRepository itemRepository;

    private final KakaoTokenRepository kakaoTokenRepository;
    private final NaverTokenRepository naverTokenRepository;

    private final EntityManager entityManager;

    @Transactional
    public ValidationStatus signup(String email, UserInfoDto userInfoDto) {
        log.info("signup으로 온 email: " + email);

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
                .build();
        userRepository.save(user);
        log.info("User 저장 완료 - id: {}", user.getId());
        return user;
    }

    private void createStar(User user, UserInfoDto userInfoDto) {
        Star star = new Star();
        star.setUser(user);

        Optional<StarBackground> background = starBackgroundRepository.findByName(userInfoDto.getBackgroundName());
        background.ifPresent(star::setBackground);

        starRepository.save(star);
        log.info("Star 세팅 완료 - id: {}", star.getId());
    }

    private void createCharacter(User user, UserInfoDto userInfoDto) {
        Face face = createFace(userInfoDto);
        Outfit outfit = createOutfit(userInfoDto);
        Item item = createItem(userInfoDto);

        Characters character = new Characters();
        character.setUser(user);
        character.setFace(face);
        character.setOutfit(outfit);
        character.setItem(item);

        characterRepository.save(character);
        log.info("Character 세팅 완료 - id: {}", character.getId());
    }

    private Face createFace(UserInfoDto userInfoDto) {
        Face face = new Face();
        face.setSkin(skinRepository.findByName(userInfoDto.getFace().getSkinImg()).orElseThrow());
        face.setExpression(expressionRepository.findByName(userInfoDto.getFace().getExpressionImg()).orElseThrow());
        face.setHair(hairRepository.findByName(userInfoDto.getFace().getHairImg()).orElseThrow());
        return face;
    }

    private Outfit createOutfit(UserInfoDto userInfoDto) {
        Outfit outfit = new Outfit();
        outfit.setTop(topRepository.findByName(userInfoDto.getOutfit().getTopImg()).orElseThrow());
        outfit.setBottom(bottomRepository.findByName(userInfoDto.getOutfit().getBottomImg()).orElseThrow());
        outfit.setShoes(shoesRepository.findByName(userInfoDto.getOutfit().getShoesImg()).orElseThrow());
        return outfit;
    }

    private Item createItem(UserInfoDto userInfoDto) {
        Item item = new Item();
        item.setHead(headRepository.findByName(userInfoDto.getItem().getHeadImg()).orElse(null));
        item.setEyes(eyesRepository.findByName(userInfoDto.getItem().getEyesImg()).orElse(null));
        item.setEars(earsRepository.findByName(userInfoDto.getItem().getEarsImg()).orElse(null));
        item.setNeck(neckRepository.findByName(userInfoDto.getItem().getNeckImg()).orElse(null));
        item.setLeftWrist(leftWristRepository.findByName(userInfoDto.getItem().getLeftWristImg()).orElse(null));
        item.setRightWrist(rightWristRepository.findByName(userInfoDto.getItem().getRightWristImg()).orElse(null));
        item.setLeftHand(leftHandRepository.findByName(userInfoDto.getItem().getLeftHandImg()).orElse(null));
        item.setRightHand(rightHandRepository.findByName(userInfoDto.getItem().getRightHandImg()).orElse(null));
        return item;
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
