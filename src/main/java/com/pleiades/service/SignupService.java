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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

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

    @Autowired
    public SignupService(UserRepository userRepository, StarRepository starRepository, CharacterRepository characterRepository,
                         SkinRepository skinRepository, ExpressionRepository expressionRepository, HairRepository hairRepository,
                         TopRepository topRepository, BottomRepository bottomRepository, ShoesRepository shoesRepository, ItemRepository itemRepository,
                         KakaoTokenRepository kakaoTokenRepository, NaverTokenRepository naverTokenRepository, StarBackgroundRepository starBackgroundRepository, HeadRepository headRepository, EyesRepository eyesRepository, EarsRepository earsRepository, NeckRepository neckRepository, LeftWristRepository leftWristRepository, RightWristRepository rightWristRepository, LeftHandRepository leftHandRepository, RightHandRepository rightHandRepository) {
        this.userRepository = userRepository; this.starRepository = starRepository; this.characterRepository = characterRepository;
        this.skinRepository = skinRepository; this.expressionRepository = expressionRepository; this.hairRepository = hairRepository;
        this.topRepository = topRepository; this.bottomRepository = bottomRepository; this.shoesRepository = shoesRepository;
        this.itemRepository = itemRepository;
        this.kakaoTokenRepository = kakaoTokenRepository; this.naverTokenRepository = naverTokenRepository;
        this.starBackgroundRepository = starBackgroundRepository;
        this.headRepository = headRepository; this.eyesRepository = eyesRepository; this.earsRepository = earsRepository; this.neckRepository = neckRepository;
        this.leftWristRepository = leftWristRepository; this.rightWristRepository = rightWristRepository;
        this.leftHandRepository = leftHandRepository; this.rightHandRepository = rightHandRepository;
    }

    public void signup(String email, SignUpDto signUpDto) {
        User user = new User();
        setNewUser(user, email, signUpDto);
        userRepository.save(user);

        Optional<NaverToken> naverToken = naverTokenRepository.findByUserEmail(email);
        Optional<KakaoToken> kakaoToken = kakaoTokenRepository.findByEmail(email);

        naverToken.ifPresent(token -> setNaverToken(token, user));
        kakaoToken.ifPresent(token -> setKakaoToken(token, user));

        if (naverToken.isEmpty() && kakaoToken.isEmpty()) {
            // 음 안 되는데
        }

        setStar(user, signUpDto);

        setCharacter(user, signUpDto);
    }

    private void setNewUser(User user, String email, SignUpDto signUpDto) {
        user.setId(signUpDto.getUserId());
        user.setEmail(email);
        user.setUserName(signUpDto.getUserName());
        user.setBirthDate(signUpDto.getBirthDate());
        user.setCreatedDate(LocalDate.now());
        user.setImgPath(signUpDto.getImgPath());
    }

    private void setNaverToken(NaverToken naverToken, User user) {
        naverToken.setUser(user);
        naverTokenRepository.save(naverToken);
    }

    private void setKakaoToken(KakaoToken kakaoToken, User user) {
        kakaoToken.setUser(user);
        kakaoTokenRepository.save(kakaoToken);
    }

    private void setStar(User user, SignUpDto signUpDto) {
        Star star = new Star();
        star.setUser(user);
        Optional<StarBackground> background = starBackgroundRepository.findByName(signUpDto.getBackgroundName());
        background.ifPresent(star::setBackground);
        starRepository.save(star);

        log.info("star saved");
    }

    private void setCharacter(User user, SignUpDto signUpDto) {
        Optional<Skin> skin = skinRepository.findByName(signUpDto.getFace().getSkinImg());
        Optional<Expression> expression = expressionRepository.findByName(signUpDto.getFace().getExpressionImg());
        Optional<Hair> hair = hairRepository.findByName(signUpDto.getFace().getHairImg());

        Optional<Top> top = topRepository.findByName(signUpDto.getOutfit().getTopImg());
        Optional<Bottom> bottom = bottomRepository.findByName(signUpDto.getOutfit().getBottomImg());
        Optional<Shoes> shoes = shoesRepository.findByName(signUpDto.getOutfit().getShoesImg());

        Optional<Head> head = headRepository.findByName(signUpDto.getItem().getHeadImg());
        Optional<Eyes> eyes = eyesRepository.findByName(signUpDto.getItem().getEyesImg());
        Optional<Ears> ears = earsRepository.findByName(signUpDto.getItem().getEarsImg());
        Optional<Neck> neck = neckRepository.findByName(signUpDto.getItem().getNeckImg());
        Optional<LeftWrist> leftWrist = leftWristRepository.findByName(signUpDto.getItem().getLeftWristImg());
        Optional<RightWrist> rightWrist = rightWristRepository.findByName(signUpDto.getItem().getRightWristImg());
        Optional<LeftHand> leftHand = leftHandRepository.findByName(signUpDto.getItem().getLeftHandImg());
        Optional<RightHand> rightHand = rightHandRepository.findByName(signUpDto.getItem().getRightHandImg());

        Characters character = new Characters();
        Face face = new Face();
        Outfit outfit = new Outfit();
        Item item = new Item();

        character.setUser(user);

        face.setSkin(skin.get());
        face.setExpression(expression.get());
        face.setHair(hair.get());

        character.setFace(face);

        outfit.setTop(top.get());
        outfit.setBottom(bottom.get());
        outfit.setShoes(shoes.get());

        character.setOutfit(outfit);

        item.setHead(head.get());
        item.setEyes(eyes.get());
        item.setEars(ears.get());
        item.setNeck(neck.get());
        item.setLeftWrist(leftWrist.get());
        item.setRightWrist(rightWrist.get());
        item.setLeftHand(leftHand.get());
        item.setRightHand(rightHand.get());

        character.setItem(item);

        characterRepository.save(character);

        log.info("character saved");
    }
}
