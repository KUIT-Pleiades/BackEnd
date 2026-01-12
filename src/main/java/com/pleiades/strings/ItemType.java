package com.pleiades.strings;

import lombok.Getter;

@Getter
public enum ItemType {
    // face
    SKIN_COLOR("SKIN", ItemCategory.FACE),
    HAIR("HAIR", ItemCategory.FACE),
    EYES("EYES", ItemCategory.FACE),
    NOSE("NOSE", ItemCategory.FACE),
    MOUTH("MOUTH", ItemCategory.FACE),
    MOLE("MOLE", ItemCategory.FACE),

    // outfit
    TOP("TOP", ItemCategory.FASHION),
    BOTTOM("BOTTOM", ItemCategory.FASHION),
    SET("SET", ItemCategory.FASHION),
    SHOES("SHOES", ItemCategory.FASHION),

    // item (accessory)
    HEAD("HEAD", ItemCategory.FASHION),
    EYES_ITEM("EYES_ITEM", ItemCategory.FASHION),
    NECK("NECK", ItemCategory.FASHION),
    EARS("EARS", ItemCategory.FASHION),
    LEFT_HAND("LEFT_HAND", ItemCategory.FASHION),
    LEFT_WRIST("LEFT_WRIST", ItemCategory.FASHION),
    RIGHT_HAND("RIGHT_HAND", ItemCategory.FASHION),
    RIGHT_WRIST("RIGHT_WRIST", ItemCategory.FASHION),

    // bg
    STAR_BG("STAR", ItemCategory.BG),
    STATION_BG("STATION", ItemCategory.BG),

    DEFAULT("DEFAULT", ItemCategory.DEFAULT);

    private final String type;
    private final ItemCategory category;

    ItemType(String type, ItemCategory category) {
        this.type = type;
        this.category = category;
    }

    public ItemCategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return type;
    }

    public static ItemType fromString(String type) {
        for (ItemType it : ItemType.values()) {
            if (it.type.equalsIgnoreCase(type)) {
                return it;
            }
        }
        throw new IllegalArgumentException("Invalid item type: " + type);
    }
}
