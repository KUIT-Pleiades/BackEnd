package com.pleiades.config;

import com.pleiades.entity.Friend;
import com.pleiades.entity.StarBackground;
import com.pleiades.entity.User;
import com.pleiades.entity.character.Item.*;
import com.pleiades.entity.character.face.Expression;
import com.pleiades.entity.character.face.Hair;
import com.pleiades.entity.character.face.Skin;
import com.pleiades.entity.character.outfit.Bottom;
import com.pleiades.entity.character.outfit.Shoes;
import com.pleiades.entity.character.outfit.Top;
import com.pleiades.repository.FriendRepository;
import com.pleiades.repository.StarBackgroundRepository;
import com.pleiades.repository.UserRepository;
import com.pleiades.repository.character.face.ExpressionRepository;
import com.pleiades.repository.character.face.HairRepository;
import com.pleiades.repository.character.face.SkinRepository;
import com.pleiades.repository.character.item.*;
import com.pleiades.repository.character.outfit.BottomRepository;
import com.pleiades.repository.character.outfit.ShoesRepository;
import com.pleiades.repository.character.outfit.TopRepository;
import com.pleiades.strings.FriendStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class DataInitializer {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    private final SkinRepository skinRepository;
    private final ExpressionRepository expressionRepository;
    private final HairRepository hairRepository;

    private final HeadRepository headRepository;
    private final NeckRepository neckRepository;
    private final EyesRepository eyesRepository;
    private final EarsRepository earsRepository;
    private final LeftHandRepository leftHandRepository;
    private final RightHandRepository rightHandRepository;
    private final LeftWristRepository leftWristRepository;
    private final RightWristRepository rightWristRepository;

    private final TopRepository topRepository;
    private final BottomRepository bottomRepository;
    private final ShoesRepository shoesRepository;

    private final StarBackgroundRepository starBackgroundRepository;

    private final String IPFS_URL = System.getenv("IPFS_URL");


    @PostConstruct
    public void initData() {
        saveUser(); saveFriend();
        saveSkin(); saveExpression(); saveHair();
        saveItem();
        saveTop(); saveBottom(); saveShoes();
        saveStarBackground();
    }

    private void saveUser() {
        List<User> users = List.of(
                new User("woogie", "wook2442@naver.com", "강연욱이", LocalDate.of(2000, 2, 4), LocalDate.of(2025, 2, 14), "profile_01", "character_01", "refresh", 0L),
                new User("yuna1217", "yuna569@naver.com", "yuna", LocalDate.of(2003, 12, 17), LocalDate.of(2025, 2, 14), "profile_01", "character_01", "refresh", 0L),
                new User("danpung628", "danpung628@gmail.com", "원우", LocalDate.of(2000, 6, 28), LocalDate.of(2025, 2, 14), "profile_01", "character_01", "refresh", 0L),
                new User("lylylylh", "yh81260@naver.com", "yoonhee", LocalDate.of(2002, 10, 4), LocalDate.of(2025, 2, 3), "profile_01", "character_01", "refresh1", 0L),
                new User("user2", "user2@naver.com", "jeongyoon", LocalDate.of(2002, 1, 29), LocalDate.of(2025, 2, 4), "profile_02", "character_02", "refresh2", 0L),
                new User("user3", "user3@naver.com", "sejin", LocalDate.of(2002, 4, 17), LocalDate.of(2025, 2, 5), "profile_03", "character_03", "refresh3", 0L),
                new User("user4", "user4@naver.com", "youngeun", LocalDate.of(2002, 12, 2), LocalDate.of(2025, 2, 3), "profile_04", "character_04", "refresh4", 0L),
                new User("user5", "user5@naver.com", "sangeun", LocalDate.of(2002, 9, 27), LocalDate.of(2025, 2, 6), "profile_05", "character_05", "refresh5", 0L),
                new User("user6", "user6@naver.com", "taeun", LocalDate.of(2002, 1, 3), LocalDate.of(2025, 2, 7), "profile_06", "character_06", "refresh6", 0L),
                new User("user7", "user7@naver.com", "hayeon", LocalDate.of(2002, 11, 14), LocalDate.of(2025, 2, 4), "profile_07", "character_07", "refresh7", 0L)
        );

        userRepository.saveAll(users);
        userRepository.flush();
    }

    private void saveFriend() {

        List<Friend> friends = List.of(
                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 11, 9, 46, 5))
                        .sender(userRepository.findById("user2").orElseThrow())
                        .receiver(userRepository.findById("lylylylh").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 11, 19, 46, 5))
                        .sender(userRepository.findById("user3").orElseThrow())
                        .receiver(userRepository.findById("lylylylh").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 12, 9, 46, 5))
                        .sender(userRepository.findById("lylylylh").orElseThrow())
                        .receiver(userRepository.findById("user4").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 12, 11, 46, 5))
                        .sender(userRepository.findById("lylylylh").orElseThrow())
                        .receiver(userRepository.findById("user5").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 12, 12, 46, 5))
                        .sender(userRepository.findById("user4").orElseThrow())
                        .receiver(userRepository.findById("user3").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 12, 14, 46, 5))
                        .sender(userRepository.findById("user5").orElseThrow())
                        .receiver(userRepository.findById("user4").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.ACCEPTED).createdAt(LocalDateTime.of(2025, 2, 13, 9, 46, 5))
                        .sender(userRepository.findById("user6").orElseThrow())
                        .receiver(userRepository.findById("user5").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 14, 9, 46, 5))
                        .sender(userRepository.findById("user7").orElseThrow())
                        .receiver(userRepository.findById("user6").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 14, 16, 46, 5))
                        .sender(userRepository.findById("user2").orElseThrow())
                        .receiver(userRepository.findById("user7").orElseThrow()).build(),

                Friend.builder().status(FriendStatus.PENDING).createdAt(LocalDateTime.of(2025, 2, 14, 20, 46, 5))
                        .sender(userRepository.findById("user7").orElseThrow())
                        .receiver(userRepository.findById("lylylylh").orElseThrow()).build()
        );

        friendRepository.saveAll(friends);
    }


    private void saveSkin() {
        String[] skins = {"skin_01", "skin_02", "skin_03", "skin_04", "skin_05", "skin_06", "skin_07"};
        for (String name : skins) {
            Skin skin = new Skin();
            skin.setName(name);
            skinRepository.save(skin);
        }
    }
    private void saveExpression() {
        String[] expressions = {"face_01", "face_02", "face_03", "face_04", "face_05", "face_06", "face_07", "face_08", "face_09"};
        for (String name : expressions) {
            Expression expression = new Expression();
            expression.setName(name);
            expressionRepository.save(expression);
        }
    }
    private void saveHair() {
        String[] hairs = {"hair_01", "hair_02", "hair_03", "hair_04", "hair_05", "hair_06", "hair_07", "hair_08", "hair_09"};
        for (String name : hairs) {
            Hair hair = new Hair();
            hair.setName(name);
            hairRepository.save(hair);
        }
    }
    private void saveItem() {
        String[] items = {"acc1_01", "acc2_01", "acc3_01", "acc4_01", "acc5_01", "acc7_01", "acc1_02", "fas1_01", "fas1_02", "fas1_03", "fas4_01"};        // acc6_01이 없음
        for (String name : items) {
            String number = name.substring(name.indexOf("acc")+3, name.length()-3);
            switch (number) {
                case "1":
                    Head head = new Head();
                    head.setName(name);
                    headRepository.save(head);
                    break;
                case "2":
                    Eyes eyes = new Eyes();
                    eyes.setName(name);
                    eyesRepository.save(eyes);
                    break;
                case "3":
                    Ears ears = new Ears();
                    ears.setName(name);
                    earsRepository.save(ears);
                    break;
                case "4":
                    Neck neck = new Neck();
                    neck.setName(name);
                    neckRepository.save(neck);
                    break;
                case "5":
                    LeftWrist leftWrist = new LeftWrist();
                    leftWrist.setName(name);
                    leftWristRepository.save(leftWrist);
                    break;
                case "6":
                    RightWrist rightWrist = new RightWrist();
                    rightWrist.setName(name);
                    rightWristRepository.save(rightWrist);
                    break;
                case "7":
                    LeftHand leftHand = new LeftHand();
                    leftHand.setName(name);
                    leftHandRepository.save(leftHand);
                    break;
                case "8":
                    RightHand rightHand = new RightHand();
                    rightHand.setName(name);
                    rightHandRepository.save(rightHand);
                    break;
                default:
                    break;
            }
        }
    }
    private void saveTop() {
        String[] tops = {"top_01", "top_02", "top_03", "top_04", "top_05", "top_06", "top_07", "top_08", "top_09"};
        for (String name : tops) {
            Top top = new Top();
            top.setName(name);
            topRepository.save(top);
        }
    }
    private void saveBottom() {
        String[] bottoms = {"bottom_01", "bottom_02", "bottom_03", "bottom_04", "bottom_05", "bottom_06", "bottom_07", "bottom_08"};
        for (String name : bottoms) {
            Bottom bottom = new Bottom();
            bottom.setName(name);
            bottomRepository.save(bottom);
        }
    }
    private void saveShoes() {
        String[] shoess = {"shoes_01", "shoes_02", "shoes_03", "shoes_04", "shoes_05", "shoes_06", "shoes_07", "shoes_08", "shoes_09"};
        for (String name : shoess) {
            Shoes shoes = new Shoes();
            shoes.setName(name);
            shoesRepository.save(shoes);
        }
    }
    private void saveStarBackground() {
        String[] bgimgs = {"background_01", "background_02", "background_03", "background_04", "background_05"};
        for (String name : bgimgs) {
            StarBackground bgimg = new StarBackground();
            bgimg.setName(name);
            starBackgroundRepository.save(bgimg);
        }
    }
    // todo: 배경
}
