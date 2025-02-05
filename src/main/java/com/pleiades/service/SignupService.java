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

    // todo: star, character 저장에 실패하면 user도 저장 X
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
//        User user = setNewUser(email, refreshToken);

        //
        User user = new User();
        user.setId(signUpDto.getUserId());
        user.setEmail(email);
        user.setUserName(signUpDto.getUserName());
        user.setBirthDate(signUpDto.getBirthDate());
        user.setRefreshToken(refreshToken);
        user.setCreatedDate(LocalDate.now());
        user.setImgPath(signUpDto.getImgPath());

        userRepository.save(user);
        log.info("user saved: " + user.getId());
        //

        naverToken.ifPresent(token -> setNaverToken(token, user));
        kakaoToken.ifPresent(token -> setKakaoToken(token, user));

        Star star = new Star();
        star.setUser(user);
        star.setId(user.getId());
        Optional<StarBackground> background = starBackgroundRepository.findByName(signUpDto.getBackgroundName());
        background.ifPresent(star::setBackground);
        starRepository.save(star);
        log.info("star saved: " + star.getId());

        Characters character = new Characters();
        character.setUser(user);
//
        log.info("get face");
        Optional<Skin> skin = skinRepository.findByName(signUpDto.getFace().getSkinImg());
        Optional<Expression> expression = expressionRepository.findByName(signUpDto.getFace().getExpressionImg());
        Optional<Hair> hair = hairRepository.findByName(signUpDto.getFace().getHairImg());

        log.info("get outfit");
        Optional<Top> top = topRepository.findByName(signUpDto.getOutfit().getTopImg());
        Optional<Bottom> bottom = bottomRepository.findByName(signUpDto.getOutfit().getBottomImg());
        Optional<Shoes> shoes = shoesRepository.findByName(signUpDto.getOutfit().getShoesImg());

        Face face = new Face();
        Outfit outfit = new Outfit();
        Item item = new Item();

        log.info("set face");
        face.setSkin(skin.get());
        face.setExpression(expression.get());
        face.setHair(hair.get());

        log.info("set outfit");
        outfit.setTop(top.get());
        outfit.setBottom(bottom.get());
        outfit.setShoes(shoes.get());

//        setItem(item);

        Optional<Head> head = headRepository.findByName(signUpDto.getItem().getHeadImg());
        Optional<Eyes> eyes = eyesRepository.findByName(signUpDto.getItem().getEyesImg());
        Optional<Ears> ears = earsRepository.findByName(signUpDto.getItem().getEarsImg());
        Optional<Neck> neck = neckRepository.findByName(signUpDto.getItem().getNeckImg());
        Optional<LeftWrist> leftWrist = leftWristRepository.findByName(signUpDto.getItem().getLeftWristImg());
        Optional<RightWrist> rightWrist = rightWristRepository.findByName(signUpDto.getItem().getRightWristImg());
        Optional<LeftHand> leftHand = leftHandRepository.findByName(signUpDto.getItem().getLeftHandImg());
        Optional<RightHand> rightHand = rightHandRepository.findByName(signUpDto.getItem().getRightHandImg());

        log.info("set item");
        head.ifPresent(item::setHead); eyes.ifPresent(item::setEyes); ears.ifPresent(item::setEars); neck.ifPresent(item::setNeck);
        leftWrist.ifPresent(item::setLeftWrist); rightWrist.ifPresent(item::setRightWrist);
        leftHand.ifPresent(item::setLeftHand); rightHand.ifPresent(item::setRightHand);

        log.info("set character");

        character.setFace(face);
        character.setOutfit(outfit);
        character.setItem(item);

        characterRepository.save(character);
        log.info("character saved: " + character.getId());
//

        characterRepository.save(character);

//        setStar(star);
//        setCharacter(character);

        return ValidationStatus.VALID;
    }

    private User setNewUser(String email, String refreshToken) {
//        Optional<User> existingUser = userRepository.findByEmail(email);
//
//        if (existingUser.isPresent()) {
//            log.info("Existing user found, using it: " + existingUser.get().getId());
//            return existingUser.get();
//        }

        User user = new User();
        user.setId(signUpDto.getUserId());
        user.setEmail(email);
        user.setUserName(signUpDto.getUserName());
        user.setBirthDate(signUpDto.getBirthDate());
        user.setRefreshToken(refreshToken);
        user.setCreatedDate(LocalDate.now());
        user.setImgPath(signUpDto.getImgPath());

        userRepository.save(user);
        log.info("user saved: " + user.getId());
        return user;
    }

    private void setNaverToken(NaverToken naverToken, User user) {
        naverToken.setUser(user);
        naverTokenRepository.save(naverToken);
    }

    private void setKakaoToken(KakaoToken kakaoToken, User user) {
        kakaoToken.setUser(user);
        kakaoTokenRepository.save(kakaoToken);
    }

    private void setStar(Star star) {
        log.info("SignupService - setStar");
        try {
            Optional<StarBackground> background = starBackgroundRepository.findByName(signUpDto.getBackgroundName());
            background.ifPresent(star::setBackground);
            log.info("star setted");
            log.info("starId: " + star.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void setCharacter(Characters character) {
        log.info("SignupService - setCharacter");

        log.info("get face");
        Optional<Skin> skin = skinRepository.findByName(signUpDto.getFace().getSkinImg());
        Optional<Expression> expression = expressionRepository.findByName(signUpDto.getFace().getExpressionImg());
        Optional<Hair> hair = hairRepository.findByName(signUpDto.getFace().getHairImg());

        log.info("get outfit");
        Optional<Top> top = topRepository.findByName(signUpDto.getOutfit().getTopImg());
        Optional<Bottom> bottom = bottomRepository.findByName(signUpDto.getOutfit().getBottomImg());
        Optional<Shoes> shoes = shoesRepository.findByName(signUpDto.getOutfit().getShoesImg());

        if (skin.isEmpty()) { log.error("need to set skin"); return; }
        if (expression.isEmpty()) { log.error("need to set expression"); return; }
        if (hair.isEmpty()) { log.error("need to set hair"); return; }
        if (top.isEmpty()) { log.error("need to set top"); return; }
        if (bottom.isEmpty()) { log.error("need to set bottom"); return; }
        if (shoes.isEmpty()) { log.error("need to set shoes"); return; }

//        if (skin.isEmpty() || expression.isEmpty() || hair.isEmpty() || top.isEmpty() || bottom.isEmpty() || shoes.isEmpty()) {
//            log.error("need to set face, outfit");
//            return false;
//        }

        Face face = new Face();
        Outfit outfit = new Outfit();
        Item item = new Item();

        log.info("set face");
        face.setSkin(skin.get());
        face.setExpression(expression.get());
        face.setHair(hair.get());

        log.info("set outfit");
        outfit.setTop(top.get());
        outfit.setBottom(bottom.get());
        outfit.setShoes(shoes.get());

        setItem(item);

        log.info("set character");

        character.setFace(face);
        character.setOutfit(outfit);
        character.setItem(item);

        characterRepository.save(character);
        log.info("character saved: " + character.getId());

    }

    private void setItem(Item item) {
        log.info("SignupService - setItem");
        Optional<Head> head = headRepository.findByName(signUpDto.getItem().getHeadImg());
        Optional<Eyes> eyes = eyesRepository.findByName(signUpDto.getItem().getEyesImg());
        Optional<Ears> ears = earsRepository.findByName(signUpDto.getItem().getEarsImg());
        Optional<Neck> neck = neckRepository.findByName(signUpDto.getItem().getNeckImg());
        Optional<LeftWrist> leftWrist = leftWristRepository.findByName(signUpDto.getItem().getLeftWristImg());
        Optional<RightWrist> rightWrist = rightWristRepository.findByName(signUpDto.getItem().getRightWristImg());
        Optional<LeftHand> leftHand = leftHandRepository.findByName(signUpDto.getItem().getLeftHandImg());
        Optional<RightHand> rightHand = rightHandRepository.findByName(signUpDto.getItem().getRightHandImg());

        log.info("set item");
        head.ifPresent(item::setHead); eyes.ifPresent(item::setEyes); ears.ifPresent(item::setEars); neck.ifPresent(item::setNeck);
        leftWrist.ifPresent(item::setLeftWrist); rightWrist.ifPresent(item::setRightWrist);
        leftHand.ifPresent(item::setLeftHand); rightHand.ifPresent(item::setRightHand);
    }
}
