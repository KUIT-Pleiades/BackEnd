package com.pleiades.service;

import com.pleiades.dto.SearchUserDto;
import com.pleiades.entity.User;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.strings.FriendStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.pleiades.dto.CharacterDto;
import com.pleiades.entity.Star;
import com.pleiades.entity.StarBackground;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    HeadRepository headRepository;
    EyesRepository eyesRepository;
    EarsRepository earsRepository;
    NeckRepository neckRepository;
    LeftWristRepository leftWristRepository;
    RightWristRepository rightWristRepository;
    LeftHandRepository leftHandRepository;
    RightHandRepository rightHandRepository;
    StarBackgroundRepository starBackgroundRepository;
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

    EntityManager entityManager;

    private CharacterDto characterDto = null;

    @Autowired
    public UserService(UserRepository userRepository, FriendRepository friendRepository, StarRepository starRepository, CharacterRepository characterRepository,
                         SkinRepository skinRepository, ExpressionRepository expressionRepository, HairRepository hairRepository,
                         TopRepository topRepository, BottomRepository bottomRepository, ShoesRepository shoesRepository, ItemRepository itemRepository,
                         KakaoTokenRepository kakaoTokenRepository, NaverTokenRepository naverTokenRepository, StarBackgroundRepository starBackgroundRepository,
                         HeadRepository headRepository, EyesRepository eyesRepository, EarsRepository earsRepository, NeckRepository neckRepository,
                         LeftWristRepository leftWristRepository, RightWristRepository rightWristRepository, LeftHandRepository leftHandRepository, RightHandRepository rightHandRepository,
                         EntityManager entityManager) {
        this.userRepository = userRepository; this.friendRepository = friendRepository; this.starRepository = starRepository; this.characterRepository = characterRepository;
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

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER_EMAIL, "login token expired"));
    }

    // todo: character 수정 후 이미지 저장
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

        return ValidationStatus.VALID;
    }

    public ValidationStatus setBackground(String email, String backgroundName) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return ValidationStatus.NONE;
        }

        Optional<Star> star = starRepository.findByUserId(user.get().getId());
        if (star.isEmpty()) {
            return ValidationStatus.NOT_VALID;
        }

        Optional<StarBackground> background = starBackgroundRepository.findByName(backgroundName);
        background.ifPresent(star.get()::setBackground);

        starRepository.save(star.get());

        return ValidationStatus.VALID;
    }

    public List<SearchUserDto> searchUser(String userId, String email) {
        List<User> users = userRepository.findByIdContainingIgnoreCase(userId);
        User currentUser = getUserByEmail(email);

        return users.stream()
                .map(user -> {
                    boolean isFriend = friendRepository
                            .isFriend(currentUser, user, FriendStatus.ACCEPTED);

                    return new SearchUserDto(
                            user.getId(),
                            user.getUserName(),
                            user.getProfileUrl(),
                            isFriend
                    );
                })
                .toList();
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
