package com.pleiades.strings;

public enum ValidationStatus {
    NONE("no object"),
    NOT_VALID("not valid"),
    VALID("valid"),
    DUPLICATE("duplicate");

    private final String status;

    private ValidationStatus(String status) {
        this.status = status;
    }
}
