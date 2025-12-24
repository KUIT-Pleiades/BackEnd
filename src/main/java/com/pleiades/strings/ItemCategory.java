package com.pleiades.strings;

public enum ItemCategory {
    FACE("face"), FASHION("fashion"), BG("background"), DEFAULT("default");

    private String category;

    ItemCategory(String category) {
        this.category = category;
    }
}
