package com.pleiades.strings;

public enum ItemTypeCategory {
    // face
    FACE_SKIN_COLOR(ItemType.SKIN_COLOR, ItemCategory.FACE), FACE_HAIR(ItemType.HAIR, ItemCategory.FACE),
    EYES(ItemType.EYES, ItemCategory.FACE), NOSE(ItemType.NOSE, ItemCategory.FACE),
    MOUTH(ItemType.MOUTH, ItemCategory.FACE), MOLE(ItemType.MOLE, ItemCategory.FACE),

    // outfit
    TOP(ItemType.TOP, ItemCategory.FASHION), BOTTOM(ItemType.BOTTOM, ItemCategory.FASHION),
    SET(ItemType.SET, ItemCategory.FASHION),SHOES(ItemType.SHOES, ItemCategory.FASHION),

    // item (accessory)
    HEAD(ItemType.HEAD, ItemCategory.FASHION), EYES_ITEM(ItemType.EYES_ITEM, ItemCategory.FASHION),
    NECK(ItemType.NECK, ItemCategory.FASHION), EARS(ItemType.EARS, ItemCategory.FASHION),
    LEFT_HAND(ItemType.LEFT_HAND, ItemCategory.FASHION),
    LEFT_WRIST(ItemType.LEFT_WRIST, ItemCategory.FASHION),
    RIGHT_HAND(ItemType.RIGHT_HAND, ItemCategory.FASHION),
    RIGHT_WRIST(ItemType.RIGHT_WRIST, ItemCategory.FASHION),

    // bg
    STAR_BG(ItemType.STAR_BG, ItemCategory.BG), STATION_BG(ItemType.STATION_BG, ItemCategory.BG),

    DEFAULT(ItemType.DEFAULT, ItemCategory.DEFAULT);

    private final ItemType type;
    private final ItemCategory category;

    ItemTypeCategory(ItemType type, ItemCategory category) {
        this.type = type;
        this.category = category;
    }

    public static ItemCategory fromType(ItemType type) {
        for (ItemTypeCategory itc : ItemTypeCategory.values()) {
            if (itc.type.equals(type)) {
                return itc.category;
            }
        }
        throw new IllegalArgumentException("Invalid item type: " + type);
    }
}
