//package com.pleiades.config;
//
//import com.pleiades.entity.character.TheItem;
//import com.pleiades.repository.character.TheItemRepository;
//import com.pleiades.repository.character.face.ExpressionRepository;
//import com.pleiades.repository.character.face.HairRepository;
//import com.pleiades.repository.character.face.SkinRepository;
//import com.pleiades.repository.character.item.*;
//import com.pleiades.repository.character.outfit.BottomRepository;
//import com.pleiades.repository.character.outfit.ShoesRepository;
//import com.pleiades.repository.character.outfit.TopRepository;
//import com.pleiades.strings.ItemType;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class TheItemInitializer implements CommandLineRunner {
//
//    private final TheItemRepository theItemRepository;
//    private final BottomRepository bottomRepository;
//    private final TopRepository topRepository;
//    private final ShoesRepository shoesRepository;
//    private final ExpressionRepository expressionRepository;
//    private final HairRepository hairRepository;
//    private final SkinRepository skinRepository;
//    private final HeadRepository headRepository;
//    private final NeckRepository neckRepository;
//    private final EarsRepository earsRepository;
//    private final EyesRepository eyesRepository;
//    private final LeftHandRepository leftHandRepository;
//    private final LeftWristRepository leftWristRepository;
//    private final RightHandRepository rightHandRepository;
//    private final RightWristRepository rightWristRepository;
//
//    @Override
//    public void run(String... args) {
//        insertFrom(bottomRepository.findAllNames(), ItemType.BOTTOM);
//        insertFrom(topRepository.findAllNames(), ItemType.TOP);
//        insertFrom(shoesRepository.findAllNames(), ItemType.SHOES);
//        insertFrom(expressionRepository.findAllNames(), ItemType.EYES);
//        insertFrom(hairRepository.findAllNames(), ItemType.HAIR);
//        insertFrom(skinRepository.findAllNames(), ItemType.SKIN_COLOR);
//        insertFrom(headRepository.findAllNames(), ItemType.HEAD);
//        insertFrom(neckRepository.findAllNames(), ItemType.NECK);
//        insertFrom(earsRepository.findAllNames(), ItemType.EARS);
//        insertFrom(eyesRepository.findAllNames(), ItemType.EYES_ITEM);
//        insertFrom(leftHandRepository.findAllNames(), ItemType.LEFT_HAND);
//        insertFrom(leftWristRepository.findAllNames(), ItemType.LEFT_WRIST);
//        insertFrom(rightHandRepository.findAllNames(), ItemType.RIGHT_HAND);
//        insertFrom(rightWristRepository.findAllNames(), ItemType.RIGHT_WRIST);
//    }
//
//    private void insertFrom(List<String> names, ItemType type) {
//        for (String name : names) {
//            if (!theItemRepository.existsByNameAndType(name, type)) {
//                TheItem item = new TheItem(null, name, type, 0L, false);
//                theItemRepository.save(item);
//            }
//        }
//    }
//}
