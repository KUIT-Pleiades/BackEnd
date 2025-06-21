//package com.pleiades.config;
//
//import com.pleiades.entity.*;
//import com.pleiades.entity.character.Item.*;
//import com.pleiades.entity.character.face.Expression;
//import com.pleiades.entity.character.face.Hair;
//import com.pleiades.entity.character.face.Skin;
//import com.pleiades.entity.character.outfit.Bottom;
//import com.pleiades.entity.character.outfit.Shoes;
//import com.pleiades.entity.character.outfit.Top;
//import com.pleiades.repository.*;
//import com.pleiades.repository.character.face.ExpressionRepository;
//import com.pleiades.repository.character.face.HairRepository;
//import com.pleiades.repository.character.face.SkinRepository;
//import com.pleiades.repository.character.item.*;
//import com.pleiades.repository.character.outfit.BottomRepository;
//import com.pleiades.repository.character.outfit.ShoesRepository;
//import com.pleiades.repository.character.outfit.TopRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//import java.util.Objects;
//
//@RequiredArgsConstructor
//@Configuration
//public class DataInitializer {
//
//    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
//
//    private final SkinRepository skinRepository;
//    private final ExpressionRepository expressionRepository;
//    private final HairRepository hairRepository;
//
//    private final HeadRepository headRepository;
//    private final NeckRepository neckRepository;
//    private final EyesRepository eyesRepository;
//    private final EarsRepository earsRepository;
//    private final LeftHandRepository leftHandRepository;
//    private final RightHandRepository rightHandRepository;
//    private final LeftWristRepository leftWristRepository;
//    private final RightWristRepository rightWristRepository;
//
//    private final TopRepository topRepository;
//    private final BottomRepository bottomRepository;
//    private final ShoesRepository shoesRepository;
//
//    private final StarBackgroundRepository starBackgroundRepository;
//    private final QuestionRepository questionRepository;
//    private final StationBackgroundRepository stationBackgroundRepository;
//
//    @PostConstruct
//    public void initData() throws IOException {
//
//        saveSkin(); saveExpression(); saveHair();
//        saveItem();
//        saveTop(); saveBottom(); saveShoes();
//        saveStarBackground();
//        saveQuestion();
//        saveStationBackground();
//    }
//
//    private void saveQuestion() {
//        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("questions.txt");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
//
//            reader.lines()
//                    .map(Question::new)
//                    .forEach(questionRepository::save);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }
//
//    // 초기 데이터
//    private void saveSkin() {
//        String[] skins = {"skin_01", "skin_02", "skin_03", "skin_04", "skin_05", "skin_06", "skin_07"};
//        for (String name : skins) {
//            Skin skin = new Skin();
//            skin.setName(name);
//            skinRepository.save(skin);
//        }
//    }
//    private void saveExpression() {
//        String[] expressions = {"face_01", "face_02", "face_03", "face_04", "face_05", "face_06", "face_07", "face_08", "face_09", "face_10", "face_11", "face_12", "face_13", "face_14", "face_15", "face_16", "face_17", "face_18", "face_19"};
//        for (String name : expressions) {
//            Expression expression = new Expression();
//            expression.setName(name);
//            expressionRepository.save(expression);
//        }
//    }
//    private void saveHair() {
//        String[] hairs = {"hair_01", "hair_02", "hair_03", "hair_04", "hair_05", "hair_06", "hair_07", "hair_08", "hair_09", "hair_10", "hair_11", "hair_12", "hair_13", "hair_14", "hair_15", "hair_16", "hair_17", "hair_18", "hair_19", "hair_20", "hair_21", "hair_22", "hair_23"};
//        for (String name : hairs) {
//            Hair hair = new Hair();
//            hair.setName(name);
//            hairRepository.save(hair);
//        }
//    }
//    private void saveItem() {
//        String[] items = {"acc1_01", "acc2_01", "acc3_01", "acc4_01", "acc5_01", "acc7_01", "acc1_02", "fas1_01", "fas1_02", "fas1_03", "fas4_01"};        // acc6_01이 없음
//        for (String name : items) {
//            String number = null;
//            if (name.startsWith("acc")) { number = name.substring(name.indexOf("acc")+3, name.length()-3); }
//            if (name.startsWith("fas")) { number = name.substring(name.indexOf("fas")+3, name.length()-3); }
//            switch (Objects.requireNonNull(number)) {
//                case "1":
//                    Head head = new Head();
//                    head.setName(name);
//                    headRepository.save(head);
//                    break;
//                case "2":
//                    Eyes eyes = new Eyes();
//                    eyes.setName(name);
//                    eyesRepository.save(eyes);
//                    break;
//                case "3":
//                    Ears ears = new Ears();
//                    ears.setName(name);
//                    earsRepository.save(ears);
//                    break;
//                case "4":
//                    Neck neck = new Neck();
//                    neck.setName(name);
//                    neckRepository.save(neck);
//                    break;
//                case "5":
//                    LeftWrist leftWrist = new LeftWrist();
//                    leftWrist.setName(name);
//                    leftWristRepository.save(leftWrist);
//                    break;
//                case "6":
//                    RightWrist rightWrist = new RightWrist();
//                    rightWrist.setName(name);
//                    rightWristRepository.save(rightWrist);
//                    break;
//                case "7":
//                    LeftHand leftHand = new LeftHand();
//                    leftHand.setName(name);
//                    leftHandRepository.save(leftHand);
//                    break;
//                case "8":
//                    RightHand rightHand = new RightHand();
//                    rightHand.setName(name);
//                    rightHandRepository.save(rightHand);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//    private void saveTop() {
//        String[] tops = {"top_01", "top_02", "top_03", "top_04", "top_05", "top_06", "top_07", "top_08", "top_09", "top_10", "top_11", "top_12"};
//        for (String name : tops) {
//            Top top = new Top();
//            top.setName(name);
//            topRepository.save(top);
//        }
//    }
//    private void saveBottom() {
//        String[] bottoms = {"bottom_01", "bottom_02", "bottom_03", "bottom_04", "bottom_05", "bottom_06", "bottom_07", "bottom_08", "bottom_09", "bottom_10", "bottom_11", "bottom_12", "bottom_13", "bottom_14", "bottom_15", "bottom_16"};
//        for (String name : bottoms) {
//            Bottom bottom = new Bottom();
//            bottom.setName(name);
//            bottomRepository.save(bottom);
//        }
//    }
//    private void saveShoes() {
//        String[] shoess = {"shoes_01", "shoes_02", "shoes_03", "shoes_04", "shoes_05", "shoes_06", "shoes_07", "shoes_08", "shoes_09"};
//        for (String name : shoess) {
//            Shoes shoes = new Shoes();
//            shoes.setName(name);
//            shoesRepository.save(shoes);
//        }
//    }
//    private void saveStarBackground() {
//        String[] bgimgs = {"background_01", "background_02", "background_03", "background_04", "background_05"};
//        for (String name : bgimgs) {
//            StarBackground bgimg = new StarBackground();
//            bgimg.setName(name);
//            starBackgroundRepository.save(bgimg);
//        }
//    }
//    private void saveStationBackground() {
//        String[] bgimgs = {"station_01", "station_02", "station_03", "station_04"};
//        for (String name : bgimgs) {
//            StationBackground bgimg = new StationBackground();
//            bgimg.setName(name);
//            stationBackgroundRepository.save(bgimg);
//        }
//        stationBackgroundRepository.flush();
//    }
//}
