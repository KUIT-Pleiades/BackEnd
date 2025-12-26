package com.pleiades.strings;

import lombok.Getter;

@Getter
public enum ItemType {
    // face
    SKIN_COLOR("skin", ItemCategory.FACE), 
    HAIR("hair", ItemCategory.FACE),
    EYES("eyes", ItemCategory.FACE), 
    NOSE("nose", ItemCategory.FACE),
    MOUTH("mouth", ItemCategory.FACE), 
    MOLE("mole", ItemCategory.FACE),

    // outfit
    TOP("top", ItemCategory.FASHION), 
    BOTTOM("bottom", ItemCategory.FASHION),
    SET("set", ItemCategory.FASHION),
    SHOES("shoes", ItemCategory.FASHION),

    // item (accessory)
    HEAD("head", ItemCategory.FASHION), 
    EYES_ITEM("eyesItem", ItemCategory.FASHION),
    NECK("neck", ItemCategory.FASHION), 
    EARS("ears", ItemCategory.FASHION),
    LEFT_HAND("left_hand", ItemCategory.FASHION),
    LEFT_WRIST("left_wrist", ItemCategory.FASHION),
    RIGHT_HAND("right_hand", ItemCategory.FASHION),
    RIGHT_WRIST("right_wrist", ItemCategory.FASHION),

    // bg
    STAR_BG("star", ItemCategory.BG), 
    STATION_BG("station", ItemCategory.BG),

    DEFAULT("default", ItemCategory.DEFAULT);

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
