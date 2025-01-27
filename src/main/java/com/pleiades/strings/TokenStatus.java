package com.pleiades.strings;

public enum TokenStatus {
    NONE("no token"),
    NOT_VALID("not valid"),
    VALID("valid");

    private final String status;

    private TokenStatus(String status) {
        this.status = status;
    }
}
