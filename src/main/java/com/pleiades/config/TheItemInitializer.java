package com.pleiades.config;

import com.pleiades.entity.character.TheItem;
import com.pleiades.repository.character.TheItemRepository;
import com.pleiades.strings.ItemType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TheItemInitializer implements CommandLineRunner {

    private final TheItemRepository theItemRepository;

    @Override
    public void run(String... args) {
        insertItems("face_eyes_", 32, ItemType.EYES, true);
        insertItems("face_nose_", 1, ItemType.NOSE, true);
        insertItems("face_mouth_", 27, ItemType.MOUTH, true);
        insertItems("face_mole_", 1, ItemType.MOLE, false);
        insertItems("face_hair_", 25, ItemType.HAIR, true);
        insertItems("face_skin_", 7, ItemType.SKIN_COLOR, true);

        insertItems("fashion_top_", 15, ItemType.TOP, false);
        insertItems("fashion_bottom_", 17, ItemType.BOTTOM, false);
        insertItems("fashion_set_", 3, ItemType.SET, false);
        insertItems("fashion_shoes_", 16, ItemType.SHOES, true);

        insertItems("fashion_acc_head_", 9, ItemType.HEAD, false);
        insertItems("fashion_acc_eyesItem_", 2, ItemType.EYES_ITEM, false);
        insertItems("fashion_acc_neck_", 3, ItemType.NECK, false);
        insertItems("fashion_acc_ears_", 3, ItemType.EARS, false);
        insertItems("fashion_acc_left_hand_", 7, ItemType.LEFT_HAND, false);
        insertItems("fashion_acc_right_hand_", 6, ItemType.RIGHT_HAND, false);
        insertItems("fashion_acc_left_wrist_", 1, ItemType.LEFT_WRIST, false);
        insertItems("fashion_acc_right_wrist_", 1, ItemType.RIGHT_WRIST, false);
    }

    private void insertItems(String prefix, int count, ItemType type, boolean isRequired) {
        for (int i = 1; i <= count; i++) {
            String padded = switch (type) {
                case EYES, MOUTH -> String.format("%03d", i);  // 3자리 패딩
                default -> String.format("%02d", i);           // 2자리 패딩
            };
            String name = prefix + padded;
            if (!theItemRepository.existsByNameAndType(name, type)) {
                TheItem item = new TheItem(null, name, type, 0L, isRequired, null);
                theItemRepository.save(item);
            }
        }
    }
//    private void insertItems(String prefix, int count, ItemType type, boolean isRequired) {
//        for (int i = 1; i <= count; i++) {
//            String name = prefix + i;
//            if (!theItemRepository.existsByNameAndType(name, type)) {
//                TheItem item = new TheItem(null, name, type, 0L, isRequired, null);
//                theItemRepository.save(item);
//            }
//        }
//    }
}