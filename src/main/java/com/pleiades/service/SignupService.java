package com.pleiades.service;

import com.pleiades.dto.SignUpDto;
import com.pleiades.entity.*;
import com.pleiades.entity.character.Characters;
import com.pleiades.entity.character.CharacterItem;
import com.pleiades.entity.character.face.Expression;
import com.pleiades.entity.character.face.Hair;
import com.pleiades.entity.character.face.Skin;
import com.pleiades.entity.character.Item.Item;
import com.pleiades.entity.character.outfit.Bottom;
import com.pleiades.entity.character.outfit.Shoes;
import com.pleiades.entity.character.outfit.Top;
import com.pleiades.repository.*;
import com.pleiades.repository.character.CharacterItemRepository;
import com.pleiades.repository.character.CharacterRepository;
import com.pleiades.repository.character.face.ExpressionRepository;
import com.pleiades.repository.character.face.HairRepository;
import com.pleiades.repository.character.face.SkinRepository;
import com.pleiades.repository.character.ItemRepository;
import com.pleiades.repository.character.outfit.BottomRepository;
import com.pleiades.repository.character.outfit.ShoesRepository;
import com.pleiades.repository.character.outfit.TopRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class SignupService {
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
    CharacterItemRepository characterItemRepository;

    KakaoTokenRepository kakaoTokenRepository;
    NaverTokenRepository naverTokenRepository;

    @Autowired
    public SignupService(UserRepository userRepository, StarRepository starRepository, CharacterRepository characterRepository,
                         SkinRepository skinRepository, ExpressionRepository expressionRepository, HairRepository hairRepository,
                         TopRepository topRepository, BottomRepository bottomRepository, ShoesRepository shoesRepository, ItemRepository itemRepository,
                         CharacterItemRepository characterItemRepository,
                         KakaoTokenRepository kakaoTokenRepository, NaverTokenRepository naverTokenRepository, StarBackgroundRepository starBackgroundRepository) {
        this.userRepository = userRepository; this.starRepository = starRepository; this.characterRepository = characterRepository;
        this.skinRepository = skinRepository; this.expressionRepository = expressionRepository; this.hairRepository = hairRepository;
        this.topRepository = topRepository; this.bottomRepository = bottomRepository; this.shoesRepository = shoesRepository;
        this.itemRepository = itemRepository;
        this.kakaoTokenRepository = kakaoTokenRepository; this.naverTokenRepository = naverTokenRepository;
        this.starBackgroundRepository = starBackgroundRepository;
    }

    public void signup(String email, SignUpDto signUpDto) {
        User user = new User();
        setNewUser(user, email, signUpDto); // id, nickname, birthDate, face, outfit, item
        userRepository.save(user);

        Optional<NaverToken> naverToken = naverTokenRepository.findByUserEmail(email);
        Optional<KakaoToken> kakaoToken = kakaoTokenRepository.findByEmail(email);

        naverToken.ifPresent(token -> setNaverToken(token, user, email));
        kakaoToken.ifPresent(token -> setKakaoToken(token, user, email));

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
    }

    private void setNaverToken(NaverToken naverToken, User user, String email) {
        // email - naver
//        NaverToken naverToken = naverTokenRepository.findByEmail(email).orElseThrow(
//                () -> new CustomException(ErrorCode.INVALID_USER_EMAIL)
//        );
        naverToken.setUser(user);
//        user.setNaverToken(naverToken);

//        userRepository.save(user);
        naverTokenRepository.save(naverToken);
    }

    private void setKakaoToken(KakaoToken kakaoToken, User user, String email) {
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

        Characters character = new Characters();
        character.setUser(user);

//        character.setSkin(skin.get());
//        character.setExpression(expression.get());
//        character.setHair(hair.get());
//
//        character.setTop(top.get());
//        character.setBottom(bottom.get());
//        character.setShoes(shoes.get());
//
//        characterRepository.save(character);
//
//        Set<String> items = signUpDto.getItem();

          // item -> CharacterItemDto
//        for (String name : items) {
//            Optional<Item> item = itemRepository.findByName(name);
//            if (item.isPresent()) {
//                CharacterItem characterItem = new CharacterItem();
//                characterItem.setCharacters(character);
//                characterItem.setItem(item.get());
//                characterItemRepository.save(characterItem);
//            }
//        }

        log.info("character saved");
    }
}
