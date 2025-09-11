package com.pleiades.strings;

public enum ItemSource {
    OFFICIAL("official"), RESALE("resale");

    private final String source;

    ItemSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
