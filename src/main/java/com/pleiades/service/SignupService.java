package com.pleiades.service;

import com.pleiades.dto.SignUpDto;
import com.pleiades.entity.*;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.Item.*;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

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
    StarBackgroundRepository starBackgroundRepository;
    UserRepository userRepository;
    StarRepository starRepository;
    CharacterRepository characterRepository;
    SkinRepository skinRepository;
    ExpressionRepository expressionRepository;
    HairRepository hairRepository;
    TopRepository topRepository;
    BottomRepository bottomRepository;
    ShoesRepository shoesRepository;
    ItemRepository itemRepository;

    KakaoTokenRepository kakaoTokenRepository;
    NaverTokenRepository naverTokenRepository;

    SignUpDto signUpDto;

    EntityManager entityManager;

    @Autowired
    public SignupService(UserRepository userRepository, StarRepository starRepository, CharacterRepository characterRepository,
                         SkinRepository skinRepository, ExpressionRepository expressionRepository, HairRepository hairRepository,
                         TopRepository topRepository, BottomRepository bottomRepository, ShoesRepository shoesRepository, ItemRepository itemRepository,
                         KakaoTokenRepository kakaoTokenRepository, NaverTokenRepository naverTokenRepository, StarBackgroundRepository starBackgroundRepository,
                         HeadRepository headRepository, EyesRepository eyesRepository, EarsRepository earsRepository, NeckRepository neckRepository,
                         LeftWristRepository leftWristRepository, RightWristRepository rightWristRepository, LeftHandRepository leftHandRepository, RightHandRepository rightHandRepository,
                         EntityManager entityManager) {
        this.userRepository = userRepository; this.starRepository = starRepository; this.characterRepository = characterRepository;
        this.skinRepository = skinRepository; this.expressionRepository = expressionRepository; this.hairRepository = hairRepository;
        this.topRepository = topRepository; this.bottomRepository = bottomRepository; this.shoesRepository = shoesRepository;
        this.itemRepository = itemRepository;
        this.kakaoTokenRepository = kakaoTokenRepository; this.naverTokenRepository = naverTokenRepository;
        this.starBackgroundRepository = starBackgroundRepository;
        this.headRepository = headRepository; this.eyesRepository = eyesRepository; this.earsRepository = earsRepository; this.neckRepository = neckRepository;
        this.leftWristRepository = leftWristRepository; this.rightWristRepository = rightWristRepository;
        this.leftHandRepository = leftHandRepository; this.rightHandRepository = rightHandRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public ValidationStatus signup(String email, SignUpDto signUpDto, String refreshToken) {
        this.signUpDto = signUpDto;

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
        User user = createUser(email, signUpDto, refreshToken);
        entityManager.flush();

        naverToken.ifPresent(token -> setNaverToken(token, user));
        kakaoToken.ifPresent(token -> setKakaoToken(token, user));

        createStar(user, signUpDto);
        createCharacter(user, signUpDto);

        return ValidationStatus.VALID;
    }

    private User createUser(String email, SignUpDto signUpDto, String refreshToken) {
        User user = User.builder()
                .id(signUpDto.getUserId())
                .email(email)
                .userName(signUpDto.getUserName())
                .birthDate(signUpDto.getBirthDate())
                .refreshToken(refreshToken)
                .createdDate(LocalDate.now())
                .imgPath(signUpDto.getImgPath())
                .build();
        userRepository.save(user);
        log.info("User 저장 완료 - id: {}", user.getId());
        return user;
    }

    private void createStar(User user, SignUpDto signUpDto) {
        Star star = new Star();
        star.setUser(user);

        Optional<StarBackground> background = starBackgroundRepository.findByName(signUpDto.getBackgroundName());
        background.ifPresent(star::setBackground);

        starRepository.save(star);
        log.info("Star 세팅 완료 - id: {}", star.getId());
    }

    private void createCharacter(User user, SignUpDto signUpDto) {
        Face face = createFace(signUpDto);
        Outfit outfit = createOutfit(signUpDto);
        Item item = createItem(signUpDto);

        Characters character = new Characters();
        character.setUser(user);
        character.setFace(face);
        character.setOutfit(outfit);
        character.setItem(item);

        characterRepository.save(character);
        log.info("Character 세팅 완료 - id: {}", character.getId());
    }

    private Face createFace(SignUpDto signUpDto) {
        Face face = new Face();
        face.setSkin(skinRepository.findByName(signUpDto.getFace().getSkinImg()).orElseThrow());
        face.setExpression(expressionRepository.findByName(signUpDto.getFace().getExpressionImg()).orElseThrow());
        face.setHair(hairRepository.findByName(signUpDto.getFace().getHairImg()).orElseThrow());
        return face;
    }

    private Outfit createOutfit(SignUpDto signUpDto) {
        Outfit outfit = new Outfit();
        outfit.setTop(topRepository.findByName(signUpDto.getOutfit().getTopImg()).orElseThrow());
        outfit.setBottom(bottomRepository.findByName(signUpDto.getOutfit().getBottomImg()).orElseThrow());
        outfit.setShoes(shoesRepository.findByName(signUpDto.getOutfit().getShoesImg()).orElseThrow());
        return outfit;
    }

    private Item createItem(SignUpDto signUpDto) {
        Item item = new Item();
        item.setHead(headRepository.findByName(signUpDto.getItem().getHeadImg()).orElse(null));
        item.setEyes(eyesRepository.findByName(signUpDto.getItem().getEyesImg()).orElse(null));
        item.setEars(earsRepository.findByName(signUpDto.getItem().getEarsImg()).orElse(null));
        item.setNeck(neckRepository.findByName(signUpDto.getItem().getNeckImg()).orElse(null));
        item.setLeftWrist(leftWristRepository.findByName(signUpDto.getItem().getLeftWristImg()).orElse(null));
        item.setRightWrist(rightWristRepository.findByName(signUpDto.getItem().getRightWristImg()).orElse(null));
        item.setLeftHand(leftHandRepository.findByName(signUpDto.getItem().getLeftHandImg()).orElse(null));
        item.setRightHand(rightHandRepository.findByName(signUpDto.getItem().getRightHandImg()).orElse(null));
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
