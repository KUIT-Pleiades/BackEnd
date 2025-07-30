package com.pleiades.strings;

import lombok.Getter;

@Getter
public enum ItemType {
    // face
    SKIN_COLOR("skinColor"), HAIR("hair"),
    EYES("eyes"), NOSE("nose"),
    MOUTH("mouth"), MOLE("mole"),

    // outfit
    TOP("top"), BOTTOM("bottom"),
    SET("set"),SHOES("shoes"),

    // item (accessory)
    HEAD("head"), EYES_ITEM("eyesItem"),
    NECK("neck"), EARS("ears"),
    LEFT_HAND("left_hand"),
    LEFT_WRIST("left_wrist"),
    RIGHT_HAND("right_hand"),
    RIGHT_WRIST("right_wrist");

    private final String type;

    ItemType(String type) {
        this.type = type;
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
